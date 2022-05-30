package com.tehang.common.utility.event.eventrecord;

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
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 领域事件记录。发送时将事件内容记录在db中
 */
@Slf4j
@Getter
@Setter(AccessLevel.PACKAGE)
@Entity
@Table(name = "domain_event_record")
public class DomainEventRecord extends AggregateRoot<String> {

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

  /** 事件发布时间，指实际发送到mq的时间(北京时间，格式为yyyy-MM-dd HH:mm:ss.SSS). */
  @Column(length = 23)
  private BjTime publishTime;

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

    record.resetCreateAndUpdateTimeToNow();
    return record;
  }
}
