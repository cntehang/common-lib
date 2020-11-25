package com.tehang.common.utility.event;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.mq.MqProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 事件发布者，用来发布事件消息到mq
 */
@Service
@AllArgsConstructor
@Slf4j
public class EventPublisher {

  // 北京/上海时间
  private static final String ZONE_SHANGHAI = "+08:00";

  private MqProducer mqProducer;
  private MqConfig mqConfig;

  /**
   * 发布领域事件
   */
  public void publish(DomainEvent event) {
    // 检查事件参数的有效性
    assertEventValid(event);

    // 填充事件的发送信息
    event.setPublisher(mqConfig.getGroupId());
    event.setPublishTime(nowOfBeijing());

    //发送事件到消息队列
    String tag = getTag(event);
    String body = JsonUtils.toJson(event);
    mqProducer.sendToQueue(tag, event.getKey(), body);

    log.debug("publish event successful, tag: {}, body: {}", tag, body);
  }

  /**
   * 检查事件参数的有效性, 包括事件类型，事件参数类型
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
