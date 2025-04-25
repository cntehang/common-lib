package com.tehang.common.utility.event.publish;

import com.tehang.common.utility.event.mq.MessageProducerException;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.mq.MqProducer;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecord;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecordJdbcRepository;
import com.tehang.common.utility.lock.DistributedLockHelper;
import com.tehang.common.utility.time.BjTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 将db中的待发送的领域事件记录，发送到mq。此服务由定时任务调用，定时任务建议每秒调用一次。
 */
@Service
@AllArgsConstructor
@Slf4j
public class SendDomainEventRecordsToMqService {

  private final MqConfig mqConfig;
  private final MqProducer mqProducer;
  private final DomainEventRecordJdbcRepository eventRecordJdbcRepository;
  private final DistributedLockHelper lockHelper;

  /**
   * 查找db中的待发送的领域事件记录，发送到mq
   * 需加锁，以防止并发调用
   */
  @Async
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void sendDomainEventRecords() {
    try {
      BjTime start = BjTime.now();

      // 查找待发送的事件记录，按时间正序排列
      List<DomainEventRecord> eventRecords = eventRecordJdbcRepository.findAllByWaitSend();
      if (eventRecords.size() > 100) {
        log.error("待发送的事务消息数量为: {}, 请开发人员检查消息系统是否正常。", eventRecords.size());
      }

      // 依次处理每个事件记录
      for (var eventRecord : eventRecords) {
        lockHelper.withLock(eventRecord.getId(), () -> {
          // 为防止重复发送消息，使用分布式锁，并再次检查记录状态
          if (eventRecordJdbcRepository.isWaitSend(eventRecord.getId())) {
            sendEventToMq(eventRecord);
          }
        });
      }

      log.debug("sendDomainEventRecords completed, size: {}, elapsed: {}s", eventRecords.size(), BjTime.elapsedSeconds(start));
    }
    catch (Exception ex) {
      log.warn("sendDomainEventRecords error, message: {}", ex.getMessage(), ex);
    }
  }

  private void sendEventToMq(DomainEventRecord eventRecord) {
    // 计算tag, topic, key, body
    String tag = getTag(eventRecord.getEventType());
    String topic = getEventTopic(eventRecord);
    String key = eventRecord.getEventKey();
    String body = eventRecord.getBody();
    Long deliverTime = getDeliverTime(eventRecord);

    try {
      // 发送消息
      mqProducer.sendToQueue(topic, tag, key, body, deliverTime);

      // 发送成功后更新记录信息
      eventRecordJdbcRepository.updateOnSendSuccess(eventRecord);

      log.debug("publish event successful, tag: {}, key: {}, body: {}", tag, key, body);
    }
    catch (MessageProducerException ex) {
      // 发布失败后更新记录信息
      eventRecordJdbcRepository.updateOnSendFailed(eventRecord, ex.getMessage());

      log.warn("publish event failed, tag: {}, key: {}, body: {}, msg: {}", tag, key, body, ex.getMessage(), ex);
    }
  }

  private String getEventTopic(DomainEventRecord eventRecord) {
    if (isBlank(eventRecord.getTopic())) {
      // 事件topic为空时，取系统配置的默认topic。
      return mqConfig.getTopic();
    }
    return eventRecord.getTopic();
  }

  private static Long getDeliverTime(DomainEventRecord eventRecord) {
    if (eventRecord.getStartDeliverTime() == null) {
      return null;
    }
    return eventRecord.getStartDeliverTime().getInnerTime().getMillis();
  }

  private String getTag(String eventType) {
    return StringUtils.trimToEmpty(mqConfig.getEventTagPrefix()) + eventType;
  }
}
