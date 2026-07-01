package com.tehang.common.utility.event.publish;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecord;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecordJdbcRepository;
import com.tehang.common.utility.time.BjTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 事务性的事件发布者。
 * 实现逻辑：只是将事件保存到db，由定时任务来发布事件消息到mq.
 */
@Service
@AllArgsConstructor
@Slf4j
public class TransactionalEventPublisher {

  private static final int MAX_EVENT_KEY_LENGTH = 100;

  private final MqConfig mqConfig;
  private final DomainEventRecordJdbcRepository eventRecordJdbcRepository;
  private final EventPublisher eventPublisher;


  /**
   * 发布领域事件, 这里是将事件保存到db，由定时任务来发送到mq。
   */
  public void publish(DomainEvent event) {
    doPublish(event, null);
  }

  /**
   * 发布领域事件, 并指定延时投递的时间(绝对时间，最大延迟时间为7天)。这里是将事件保存到db，由定时任务来发送到mq。
   * @param event 待发布的事件
   * @param startDeliverTime 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
   */
  public void publish(DomainEvent event, BjTime startDeliverTime) {
    doPublish(event, startDeliverTime);
  }

  /**
   * 发布领域事件，并使用稳定业务key保证同一事件类型下只写入一次事件记录.
   * @param event 待发布的事件
   * @param idempotentKey 稳定业务key
   * @return true表示首次发布成功，false表示重复发布已被忽略
   */
  public boolean publishOnce(DomainEvent event, String idempotentKey) {
    return publishOnce(event, idempotentKey, null);
  }

  /**
   * 发布领域事件，并使用稳定业务key保证同一事件类型下只写入一次事件记录.
   * @param event 待发布的事件
   * @param idempotentKey 稳定业务key
   * @param startDeliverTime 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
   * @return true表示首次发布成功，false表示重复发布已被忽略
   */
  public boolean publishOnce(DomainEvent event, String idempotentKey, BjTime startDeliverTime) {
    assertIdempotentKeyValid(idempotentKey);
    event.setKey(idempotentKey);
    return doPublishOnce(event, startDeliverTime);
  }

  /**
   * 不参与当前事物而直接发布事件消息，不需要保存到事件记录中异步发送。
   * 此方法同eventPublisher.publish(), 提供此方法是为了效率考虑。
   */
  public void publishDirectly(DomainEvent event) {
    eventPublisher.publish(event);
  }

  /**
   * 不参与当前事物而直接发布事件消息，不需要保存到事件记录中异步发送。
   * 此方法同eventPublisher.publish(), 提供此方法是为了效率考虑。
   */
  public void publishDirectly(DomainEvent event, BjTime startDeliverTime) {
    if (startDeliverTime == null) {
      eventPublisher.publish(event);
    }
    else {
      eventPublisher.publish(event, startDeliverTime.getInnerTime().getMillis());
    }
  }

  private boolean doPublishOnce(DomainEvent event, BjTime startDeliverTime) {
    // 检查事件参数的有效性
    assertEventValid(event);

    // 创建事件记录，并保存到db
    try {
      var eventRecord = DomainEventRecord.create(event, startDeliverTime, mqConfig.getGroupId());
      boolean published = eventRecordJdbcRepository.addOnce(eventRecord);
      if (!published) {
        log.warn("publish event ignored because duplicated, key: {}, eventType: {}", event.getKey(), event.getEventType());
      }
      return published;
    }
    catch (Exception ex) {
      var msg = "publish event failed, errorMsg: " + ex.getMessage();
      log.error(msg, ex);
      throw new SystemErrorException(msg, ex);
    }
  }

  private void doPublish(DomainEvent event, BjTime startDeliverTime) {
    // 检查事件参数的有效性
    assertEventValid(event);

    // 创建事件记录，并保存到db
    try {
      var eventRecord = DomainEventRecord.create(event, startDeliverTime, mqConfig.getGroupId());
      eventRecordJdbcRepository.add(eventRecord);
    }
    catch (Exception ex) {
      var msg = "publish event failed, errorMsg: " + ex.getMessage();
      log.error(msg, ex);
      throw new SystemErrorException(msg, ex);
    }
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

  private void assertIdempotentKeyValid(String idempotentKey) {
    if (isBlank(idempotentKey)) {
      String msg = "idempotentKey不能为空";
      log.error(msg);
      throw new SystemErrorException(msg);
    }
    if (idempotentKey.length() > MAX_EVENT_KEY_LENGTH) {
      String msg = String.format("idempotentKey长度不能超过%s", MAX_EVENT_KEY_LENGTH);
      log.error(msg);
      throw new SystemErrorException(msg);
    }
  }
}
