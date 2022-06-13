package com.tehang.common.utility.event.publish.eventrecord;

import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.baseclass.AggregateRoot;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.publish.TraceInfoHelper;
import com.tehang.common.utility.time.BjTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 领域事件记录。发送时将事件内容记录在db中，通过定时任务来发送。
 */
@Slf4j
@Getter
@Setter(AccessLevel.PACKAGE)
@Entity
@Table(name = "domain_event_record")
public class DomainEventRecord extends AggregateRoot<String> {

  // 最大发送次数：5次
  private static final int MAX_SEND_TIMES = 5;

  /** PK, uuid */
  @Id
  @Column(nullable = false, length = 50)
  private String id;

  /** 事件key */
  @Column(nullable = false, length = 100)
  private String eventKey;

  /** 事件类型，和mq中的tag保持一致(tag可能包含前缀，但eventType字段并不包含前缀) */
  @Column(nullable = false, length = 100)
  private String eventType;

  /** 事件发布者(对应于mq中的groupId). */
  @Column(length = 200)
  private String publisher;

  /** 设置消息的延时投递时间（绝对时间),最大延迟时间为7天. null表示立即投递。*/
  @Column(length = 23)
  private BjTime startDeliverTime;

  /** 发布事件所在的TraceId */
  @Column(length = 200)
  private String traceId;

  /** 事件发送的消息body。*/
  @Column(columnDefinition = "TEXT")
  private String body;

  /** 事件记录的发送状态 */
  @Column(nullable = false, length = 30)
  @Enumerated(EnumType.STRING)
  private DomainEventSendStatus status;

  /** 事件发布时间，指实际发送到mq的时间(北京时间，格式为yyyy-MM-dd HH:mm:ss.SSS). */
  @Column(length = 23)
  private BjTime publishTime;

  /** 实际发送的次数，初始为0 */
  private int count;

  // ------------- 方法 ------------

  /** 创建事件记录的工厂方法 */
  public static DomainEventRecord create(DomainEvent event, BjTime startDeliverTime, String mqGroupId) {
    var record = new DomainEventRecord();
    record.id = UUID.randomUUID().toString();
    record.eventKey = event.getKey();
    record.eventType = event.getEventType();
    record.publisher = mqGroupId;
    record.startDeliverTime = startDeliverTime;
    record.traceId = TraceInfoHelper.getCurrentTraceId();
    record.body = JsonUtils.toJson(event);
    record.status = DomainEventSendStatus.WaitSend;

    record.resetCreateAndUpdateTimeToNow();
    return record;
  }

  /** 发送成功后更新记录信息 */
  public void onSendSuccess() {
    this.status = DomainEventSendStatus.SendSuccess;
    this.count++;
    this.publishTime = BjTime.now();
    this.resetUpdateTimeToNow();
  }

  /** 发送失败后更新记录信息 */
  public void onSendFailed(String errorMsg) {
    this.count++;
    this.resetUpdateTimeToNow();

    if (this.count >= MAX_SEND_TIMES) {
      this.status = DomainEventSendStatus.SendFailed;

      log.error("发送事件消息到mq失败, key: {}, eventType: {}, errorMsg: {}", this.eventKey, this.eventType, errorMsg);
    }
  }
}
