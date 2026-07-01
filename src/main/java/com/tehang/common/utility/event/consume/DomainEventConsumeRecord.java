package com.tehang.common.utility.event.consume;

import com.tehang.common.utility.baseclass.AggregateRoot;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.time.BjTime;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.UUID;

/**
 * 领域事件消费记录.
 */
@Getter
@Setter
public class DomainEventConsumeRecord extends AggregateRoot<String> {

  /** PK, uuid. */
  @Id
  @Column(nullable = false, length = 50)
  private String id;

  /** 事件key. */
  @Column(nullable = false, length = 100)
  private String eventKey;

  /** 事件类型. */
  @Column(nullable = false, length = 100)
  private String eventType;

  /** 订阅者稳定标识. */
  @Column(nullable = false, length = 200)
  private String subscriberId;

  /** 消费状态. */
  @Column(nullable = false, length = 30)
  @Enumerated(EnumType.STRING)
  private DomainEventConsumeStatus status;

  /** 消费成功时间. */
  @Column(length = 23)
  private BjTime consumeTime;

  /** 错误信息, 预留字段. */
  @Column(length = 500)
  private String errorMessage;

  /**
   * 创建消费处理中记录.
   */
  public static DomainEventConsumeRecord createProcessing(DomainEvent event, String subscriberId) {
    var record = new DomainEventConsumeRecord();
    record.id = UUID.randomUUID().toString();
    record.eventKey = event.getKey();
    record.eventType = event.getEventType();
    record.subscriberId = subscriberId;
    record.status = DomainEventConsumeStatus.Processing;
    record.resetCreateAndUpdateTimeToNow();
    return record;
  }
}
