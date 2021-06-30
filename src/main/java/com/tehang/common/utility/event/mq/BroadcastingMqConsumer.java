package com.tehang.common.utility.event.mq;

import brave.ScopedSpan;
import brave.Tracer;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.cache.RefreshableCache;
import com.tehang.common.utility.event.subscriber.BroadcastingEventSubscriber;
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
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * 广播消息消费者.
 */
@Component
@Slf4j
public class BroadcastingMqConsumer implements CommandLineRunner, DisposableBean {

  @Autowired
  private MqConfig mqConfig;

  @Autowired
  private Tracer tracer;

  @Autowired
  private ApplicationContext applicationContext;

  /**
   * 阿里云底层的消息消费者, 在程序启动时创建并初始化.
   */
  private Consumer consumer;

  /**
   * 当前服务中广播事件的订阅者集合, key为EventType.
   */
  private ConcurrentMap<String, List<BroadcastingEventSubscriber>> allSubscribers;

  /**
   * 在SpringBoot应用程序启动后, 开启消费者订阅.
   */
  @Override
  @SuppressWarnings("all")
  public void run(String... args) throws Exception {
    // 初始化事件订阅者
    initEventSubscribers();

    if (allSubscribers.isEmpty()) {
      // 系统中无订阅者时，不需要发起mq的订阅，直接退出
      log.debug("BroadcastingMqConsumer init completed: has no BroadcastingEventSubscribers");
      return;
    }

    // 创建Consumer
    log.debug("BroadcastingMqConsumer initializing");
    Properties properties = getProperties();
    consumer = ONSFactory.createConsumer(properties);
    log.debug("BroadcastingMqConsumer created");

    // 启动Consumer
    log.debug("BroadcastingMqConsumer starting");
    consumer.start();

    //订阅多个 Tag  TagA||TagB，如果模糊订阅 *
    String consumerTags = getBroadcastingConsumerTags();
    consumer.subscribe(mqConfig.getTopic(), consumerTags, (message, context) -> {

      String tag = message.getTag();
      String key = message.getKey();
      String body = new String(message.getBody(), Charset.forName("UTF-8"));
      log.debug("BroadcastingMqConsumer start, tag:{}, key:{}, body:{}", tag, key, body);

      ScopedSpan span = tracer.startScopedSpan("mqConsumer");// start a new tracer for each message
      try {
        // 处理收到的mq消息
        processMessage(tag, body);
        return Action.CommitMessage;
      }
      catch (Exception ex) {
        String errorMsg = "BroadcastingMqConsumer error:" + ex.getMessage();
        log.error(errorMsg, ex);
        return Action.ReconsumeLater;
      }
      finally {
        span.finish(); //end span
      }
    });

    log.debug("BroadcastingMqConsumer started, consumeTags: {}", consumerTags);
  }

  /**
   * 针对一条消息的处理逻辑
   */
  private void processMessage(String tag, String body) {
    // 根据Tag得到EventType: 去掉前缀
    String eventType = getEventTypeFromTag(tag);

    // 根据EventType查找订阅者
    List<BroadcastingEventSubscriber> subscribers = this.allSubscribers.get(eventType);

    if (isNotEmpty(subscribers)) {
      // 根据第一个订阅者的EventClass反序列化得到DomainEvent参数
      // 约定：所有相同EventType的订阅者需要使用相当的事件参数
      var firstSubscriber = subscribers.get(0);
      DomainEvent event = JsonUtils.toClass(body, firstSubscriber.getEventClass());
      log.debug("DomainEvent created: {}", event);

      for (var subscriber : subscribers) {
        // 依次调用每个订阅者的处理逻辑
        log.debug("BroadcastingEventSubscriber: [{}] handleEvent: [{}] starting", subscriber.getInstanceId(), eventType);
        subscriber.handleEvent(event);
        log.debug("BroadcastingEventSubscriber: [{}] handleEvent: [{}] complete", subscriber.getInstanceId(), eventType);
      }
      log.debug("BroadcastingMqConsumer completed, tag:{}", tag);

    }
    else {
      log.error("BroadcastingMqConsumer error, unknown tag:{}", tag);
    }
  }

  private String getBroadcastingConsumerTags() {
    return this.allSubscribers.keySet().stream().map(this::getTagFromEventType).collect(Collectors.joining("||"));
  }

  /**
   * 根据EventType获取对应的tag: TagPrefix + EventType.
   */
  private String getTagFromEventType(String eventType) {
    return trimToEmpty(mqConfig.getEventTagPrefix()) + eventType;
  }

  /**
   * 根据tag获取对应的EventType: 将Tag移除tagPrefix.
   */
  private String getEventTypeFromTag(String tag) {
    return StringUtils.removeStart(tag, trimToEmpty(mqConfig.getEventTagPrefix()));
  }

  private void initEventSubscribers() {
    // 获取上下文中所有的广播订阅者
    Map<String, BroadcastingEventSubscriber> subscribersMap = applicationContext.getBeansOfType(BroadcastingEventSubscriber.class);

    // 获取所有的缓存实例
    Map<String, RefreshableCache> refreshableCaches = applicationContext.getBeansOfType(RefreshableCache.class);

    // 合并广播订阅者和所有的缓存实例
    // 将缓存对象转换为RefreshableCacheAdapter
    var subscribers = Stream.concat(subscribersMap.values().stream(), refreshableCaches.values().stream().map(RefreshableCacheAdapter::new));

    // 将订阅者按EventType进行分组
    this.allSubscribers = subscribers.collect(Collectors.groupingByConcurrent(EventSubscriber::subscribedEventType));
  }

  private Properties getProperties() {
    Properties properties = new Properties();
    properties.put(PropertyKeyConst.AccessKey, mqConfig.getAccessKey());
    properties.put(PropertyKeyConst.SecretKey, mqConfig.getAccessSecretKey());
    properties.put(PropertyKeyConst.NAMESRV_ADDR, mqConfig.getNameServer());

    properties.put(PropertyKeyConst.GROUP_ID, getBroadcastingGroupIdInsured());
    properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.BROADCASTING);
    properties.put(PropertyKeyConst.ConsumeThreadNums, 10);
    return properties;
  }

  private String getBroadcastingGroupIdInsured() {
    String result = mqConfig.getBroadcastingGroupId();
    if (isBlank(result)) {
      String msg = "missing mq config: broadcastingGroupId";
      log.debug(msg);
      throw new SystemErrorException(msg);
    }
    return result;
  }

  @Override
  public void destroy() {
    if (consumer != null) {
      consumer.shutdown();
      log.debug("MqConsumer shutdown");
    }
  }
}