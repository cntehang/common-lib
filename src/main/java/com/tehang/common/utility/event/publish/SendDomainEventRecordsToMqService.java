package com.tehang.common.utility.event.publish;

import com.tehang.common.utility.db.jpa.OpenJpaSession;
import com.tehang.common.utility.event.mq.MessageProducerException;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.mq.MqProducer;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecord;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecordRepository;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventSendStatus;
import com.tehang.common.utility.lock.Locked;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 将db中的待发送的领域事件记录，发送到mq。此服务由定时任务调用，定时任务建议每秒调用一次。
 */
@Service
@AllArgsConstructor
@Slf4j
public class SendDomainEventRecordsToMqService {

  private final MqConfig mqConfig;
  private final MqProducer mqProducer;
  private final DomainEventRecordRepository eventRecordRepository;

  /**
   * 查找db中的待发送的领域事件记录，发送到mq
   * 需加锁，以防止并发调用
   */
  @OpenJpaSession
  @Locked(blocked = true)
  public void sendDomainEventRecords() {
    // 查找待发送的事件记录，按时间正序排列
    List<DomainEventRecord> eventRecords = eventRecordRepository.findByStatusOrderByCreateTimeAsc(DomainEventSendStatus.WaitSend);
    log.debug("find eventRecords to Send, size: {}", eventRecords.size());

    // 依次处理每个事件记录
    eventRecords.forEach(this::sendEventToMq);
  }

  private void sendEventToMq(DomainEventRecord eventRecord) {
    // 计算tag, key, body
    String tag = getTag(eventRecord.getEventType());
    String key = eventRecord.getEventKey();
    String body = eventRecord.getBody();

    try {
      // 发送消息
      mqProducer.sendToQueue(tag, key, body, eventRecord.getStartDeliverTime());

      // 发送成功后更新记录信息
      eventRecord.onSendSuccess();
      eventRecordRepository.save(eventRecord);
      log.debug("publish event successful, tag: {}, key: {}, body: {}", tag, key, body);
    }
    catch (MessageProducerException ex) {
      // 发布失败后更新记录信息
      eventRecord.onSendFailed();
      eventRecordRepository.save(eventRecord);
      log.debug("publish event failed, tag: {}, key: {}, body: {}, msg: {}", tag, key, body, ex.getMessage(), ex);
    }
  }

  private String getTag(String eventType) {
    return StringUtils.trimToEmpty(mqConfig.getEventTagPrefix()) + eventType;
  }
}
