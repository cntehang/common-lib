package com.tehang.common.utility.event.publish;

import com.google.common.collect.Lists;
import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.mq.MessageProducerException;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.mq.MqProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 事件发布者，用来发布事件消息到mq.
 */
@Service
@AllArgsConstructor
@Slf4j
public class EventPublisher {

  // 北京/上海时间
  private static final String ZONE_SHANGHAI = "+08:00";

  // 发送消息时每次重试的延迟秒数, 依次延迟1秒，3秒，10秒
  private static final List<Integer> RE_TRY_SEND_MESSAGE_DELAYS_SECONDS = Lists.newArrayList(1, 3, 10);

  private final MqProducer mqProducer;

  private final MqConfig mqConfig;

  /**
   * 发布领域事件.
   */
  public void publish(DomainEvent event) {
    doPublish(event, null);
  }

  /**
   * 发布领域事件, 并指定延时投递的时间（单位毫秒）。
   * @param event 待发布的事件
   * @param startDeliverTime 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
   *  1. 延迟投递: 延迟3s投递, 设置为: System.currentTimeMillis() + 3000;
   *  2. 定时投递: 2016-02-01 11:30:00投递, 设置为: new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-02-01 11:30:00").getTime()
   */
  public void publish(DomainEvent event, long startDeliverTime) {
    doPublish(event, startDeliverTime);
  }

  private void doPublish(DomainEvent event, Long startDeliverTime) {
    // 检查事件参数的有效性
    assertEventValid(event);

    // 填充事件的发送信息
    event.setPublisher(mqConfig.getGroupId());
    event.setPublishTime(nowOfBeijing());
    event.setTraceId(TraceInfoHelper.getCurrentTraceId());

    //发送事件到消息队列, 并在失败时进行重试
    String tag = getTag(event);
    String body = JsonUtils.toJson(event);
    sendEventMessageWithRetry(event, tag, body, startDeliverTime);
  }

  private void sendEventMessageWithRetry(DomainEvent event, String tag, String body, Long startDeliverTime) {
    int retryTimes = 0;
    while (true) {
      try {
        // 发送消息，发送成功后直接返回
        mqProducer.sendToQueue(tag, event.getKey(), body, startDeliverTime);
        log.debug("publish event successful, tag: {}, body: {}", tag, body);
        return;
      }
      catch (MessageProducerException ex) {
        // 发布事件失败，需要进行3次重试
        log.debug("publish event failed, tag: {}, body: {}, msg: {}", tag, body, ex.getMessage(), ex);
        if (retryTimes < RE_TRY_SEND_MESSAGE_DELAYS_SECONDS.size()) {
          long sleepMillis = RE_TRY_SEND_MESSAGE_DELAYS_SECONDS.get(retryTimes) * 1000;
          sleepForMillis(sleepMillis);
          retryTimes++;
        }
        else {
          // 重试次数达到最大值，抛出异常，发布事件失败！
          log.error("publish event finally failed, tag: {}, body: {}, msg: {}", tag, body, ex.getMessage(), ex);
          throw ex;
        }
      }
    }
  }

  private void sleepForMillis(long sleepMillis) {
    try {
      Thread.sleep(sleepMillis);
    }
    catch (InterruptedException ex) {
      log.warn(ex.getMessage(), ex);
      throw new SystemErrorException(ex.getMessage(), ex);
    }
  }

  /**
   * 检查事件参数的有效性, 包括事件类型，事件参数类型.
   */
  private void assertEventValid(DomainEvent event) {
    if (isBlank(event.getKey())) {
      String msg = "event.key不能为空";
      log.error(msg);
      throw new SystemErrorException(msg);
    }
    if (isBlank(event.getEventType())) {
      String msg = "event.eventType不能为空";
      log.error(msg);
      throw new SystemErrorException(msg);
    }
  }

  private String getTag(DomainEvent event) {
    return StringUtils.trimToEmpty(mqConfig.getEventTagPrefix()) + event.getEventType();
  }

  private static String nowOfBeijing() {
    return DateTime.now(DateTimeZone.forID(ZONE_SHANGHAI)).toString("yyyy-MM-dd HH:mm:ss.SSS");
  }
}
