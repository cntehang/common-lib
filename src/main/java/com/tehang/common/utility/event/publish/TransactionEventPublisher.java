package com.tehang.common.utility.event.publish;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.eventrecord.DomainEventRecord;
import com.tehang.common.utility.event.eventrecord.DomainEventRecordRepository;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.mq.TransactionMqProducer;
import com.tehang.common.utility.time.BjTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * 事务消息事件发布者，用来发布事件事务消息到mq.
 */
@Service
@AllArgsConstructor
@Slf4j
public class TransactionEventPublisher {

  private final MqConfig mqConfig;
  private final DomainEventRecordRepository eventRecordRepository;
  private final TransactionMqProducer transactionMqProducer;

  /**
   * 发布领域事件
   */
  @Transactional
  public void publish(DomainEvent event) {
    doPublish(event, null);
  }

  /**
   * 发布领域事件, 并指定延时投递的时间(绝对时间，最大延迟时间为7天)。这里是将事件保存到db，由定时任务来发送到mq。
   * @param event 待发布的事件
   * @param startDeliverTime 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
   */
  @Transactional
  public void publish(DomainEvent event, BjTime startDeliverTime) {
    doPublish(event, startDeliverTime);
  }

  private void doPublish(DomainEvent event, BjTime startDeliverTime) {
    // 检查事件参数的有效性
    assertEventValid(event);

    // 创建事件记录，并保存到db
    var eventRecord = DomainEventRecord.create(event, startDeliverTime, mqConfig.getGroupId());
    eventRecordRepository.save(eventRecord);

    // 计算tag, key, body
    String tag = getTag(eventRecord.getEventType());
    String key = eventRecord.getEventKey();
    String body = eventRecord.getBody();
    Long deliverTime = getDeliverTime(eventRecord);

    // 发送消息
    transactionMqProducer.sendToQueue(tag, key, body, deliverTime);
    log.debug("publish event successful, tag: {}, key: {}, body: {}", tag, key, body);
  }

  /** 检查事件参数的有效性, 包括事件类型，事件参数类型. */
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

  private static Long getDeliverTime(DomainEventRecord eventRecord) {
    if (eventRecord.getStartDeliverTime() == null) {
      return null;
    }
    return eventRecord.getStartDeliverTime().getInnerTime().getMillis();
  }

  private String getTag(String eventType) {
    return trimToEmpty(mqConfig.getEventTagPrefix()) + eventType;
  }
}
