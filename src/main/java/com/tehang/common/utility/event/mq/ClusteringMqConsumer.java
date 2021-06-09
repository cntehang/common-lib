package com.tehang.common.utility.event.mq;

import brave.ScopedSpan;
import brave.Tracer;
import brave.propagation.TraceContext;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.subscriber.ClusteringEventSubscriber;
import com.tehang.common.utility.event.subscriber.EventSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * 集群消息消费者
 */
@Component
@Slf4j
public class ClusteringMqConsumer implements CommandLineRunner, DisposableBean {

  @Autowired
  private MqConfig mqConfig;

  @Autowired
  private Tracer tracer;

  @Autowired
  private ApplicationContext applicationContext;

  /**
   * 阿里云底层的消息消费者, 在程序启动时创建并初始化
   */
  private Consumer consumer;

  /**
   * 当前服务中集群事件的订阅者集合, key为EventType
   */
  private ConcurrentMap<String, List<ClusteringEventSubscriber>> allSubscribers;

  /**
   * 在SpringBoot应用程序启动后, 开启消费者订阅
   */
  @Override
  @SuppressWarnings("all")
  public void run(String... args) throws Exception {
    // 初始化事件订阅者
    initEventSubscribers();

    if (allSubscribers.isEmpty()) {
      // 系统中无订阅者时，不需要发起mq的订阅，直接退出
      log.debug("ClusteringMqConsumer init completed: has no ClusteringEventSubscribers");
      return;
    }

    // 创建Consumer
    log.debug("ClusteringMqConsumer initializing");
    Properties properties = getProperties();
    consumer = ONSFactory.createConsumer(properties);
    log.debug("ClusteringMqConsumer created");

    // 启动Consumer
    log.debug("ClusteringMqConsumer starting");
    consumer.start();

    //订阅多个 Tag  TagA||TagB，如果模糊订阅 *
    String consumerTags = getClusteringConsumerTags();
    consumer.subscribe(mqConfig.getTopic(), consumerTags, (message, context) -> {

      String tag = message.getTag();
      String key = message.getKey();
      String body = new String(message.getBody(), Charset.forName("UTF-8"));
      log.debug("ClusteringMqConsumer start, tag:{}, key:{}, body:{}", tag, key, body);

      try {
        // 处理收到的mq消息
        processMessage(tag, body);
        return Action.CommitMessage;
      }
      catch (Exception ex) {
        String errorMsg = "ClusteringMqConsumer error:" + ex.getMessage();
        log.error(errorMsg, ex);
        return Action.ReconsumeLater;
      }
    });

    log.debug("ClusteringMqConsumer started, consumeTags: {}", consumerTags);
  }

  /**
   * 针对一条消息的处理逻辑
   */
  private void processMessage(String tag, String body) {
    // 根据Tag得到EventType: 去掉前缀
    String eventType = getEventTypeFromTag(tag);

    // 根据EventType查找订阅者
    List<ClusteringEventSubscriber> subscribers = this.allSubscribers.get(eventType);

    if (isNotEmpty(subscribers)) {
      // 根据第一个订阅者的EventClass反序列化得到DomainEvent参数
      // 约定：所有相同EventType的订阅者需要使用相同的事件参数
      var firstSubscriber = subscribers.get(0);
      DomainEvent event = JsonUtils.toClass(body, firstSubscriber.getEventClass());

      // 开启新的span记录日志, traceId取自event.traceId
      ScopedSpan span = tracer.startScopedSpanWithParent("mqConsumer", createTraceContext(event));
      try {
        log.debug("DomainEvent created: {}", event);

        for (var subscriber : subscribers) {
          // 依次调用每个订阅者的处理逻辑
          log.debug("ClusteringEventSubscriber: [{}] handleEvent: [{}] starting", subscriber.getInstanceId(), eventType);
          subscriber.handleEvent(event);
          log.debug("ClusteringEventSubscriber: [{}] handleEvent: [{}] complete", subscriber.getInstanceId(), eventType);
        }
        log.debug("ClusteringMqConsumer completed, tag:{}", tag);
      }
      catch (Exception ex) {
        log.error("ClusteringMqConsumer error, msg: {}, event: {}", ex.getMessage(), event, ex);
        throw ex;
      }
      finally {
        span.finish(); //end span
      }
    }
    else {
      log.error("ClusteringMqConsumer error, unknown tag:{}", tag);
    }
  }

  /**
   * 根据event.traceId创建traceContext
   */
  private static TraceContext createTraceContext(DomainEvent event) {
    TraceContext traceContext = null;
    if (event != null && StringUtils.isNotBlank(event.getTraceId())) {
      long traceId = Long.parseLong(event.getTraceId());
      traceContext = TraceContext.newBuilder()
          .traceId(traceId)
          .spanId(traceId)
          .build();
    }
    return traceContext;
  }

  private String getClusteringConsumerTags() {
    return this.allSubscribers.keySet().stream()
        .map(this::getTagFromEventType)
        .collect(Collectors.joining("||"));
  }

  /**
   * 根据EventType获取对应的tag: TagPrefix + EventType
   */
  private String getTagFromEventType( String eventType) {
    return trimToEmpty(mqConfig.getEventTagPrefix()) + eventType;
  }

  /**
   * 根据tag获取对应的EventType: 将Tag移除tagPrefix
   */
  private String getEventTypeFromTag(String tag) {
    return StringUtils.removeStart(tag, trimToEmpty(mqConfig.getEventTagPrefix()));
  }

  private void initEventSubscribers() {
    // 获取上下文中所有的订阅者
    Map<String, ClusteringEventSubscriber> subscribersMap = applicationContext.getBeansOfType(ClusteringEventSubscriber.class);

    // 将订阅者按EventType进行分组
    this.allSubscribers = subscribersMap.values().stream()
        .collect(Collectors.groupingByConcurrent(EventSubscriber::subscribedEventType));
  }

  private Properties getProperties() {
    Properties properties = new Properties();
    properties.put(PropertyKeyConst.AccessKey, mqConfig.getAccessKey());
    properties.put(PropertyKeyConst.SecretKey, mqConfig.getAccessSecretKey());
    properties.put(PropertyKeyConst.NAMESRV_ADDR, mqConfig.getNameServer());

    properties.put(PropertyKeyConst.GROUP_ID, mqConfig.getGroupId());
    properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
    properties.put(PropertyKeyConst.ConsumeThreadNums, 10);
    return properties;
  }

  @Override
  public void destroy() {
    if (consumer != null) {
      consumer.shutdown();
      log.debug("MqConsumer shutdown");
    }
  }
}