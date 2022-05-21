package com.tehang.common.utility.event.publish;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecord;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecordRepository;
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

  private final MqConfig mqConfig;
  private final DomainEventRecordRepository eventRecordRepository;

  /**
   * 发布领域事件, 这里是将事件保存到db，由定时任务来发送到mq。
   */
  public void publish(DomainEvent event) {
    doPublish(event, null);
  }

  /**
   * 发布领域事件, 并指定延时投递的时间（单位毫秒）。 这里是将事件保存到db，由定时任务来发送到mq。
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

    // 创建事件记录，并保存到db
    var eventRecord = DomainEventRecord.create(event, startDeliverTime, mqConfig.getGroupId());
    eventRecordRepository.save(eventRecord);
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
}
