package com.tehang.common.utility.event.consume;

import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.subscriber.DatabaseIdempotentClusteringEventSubscriber;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 领域事件消费幂等服务.
 */
@Service
@AllArgsConstructor
@Slf4j
public class DomainEventConsumeService {

  private final DomainEventConsumeRecordJdbcRepository consumeRecordJdbcRepository;

  /**
   * 在同一个事务中完成消费记录占位、业务处理和成功状态更新.
   */
  @Transactional
  public boolean consume(DatabaseIdempotentClusteringEventSubscriber subscriber, DomainEvent event) {
    String subscriberId = subscriber.subscriberId();
    boolean inserted = consumeRecordJdbcRepository.insertProcessing(event, subscriberId);
    if (!inserted) {
      log.warn("事件已消费成功, 此次为重复调用, 系统自动忽略, key: {}, eventType: {}, subscriberId: {}",
          event.getKey(), event.getEventType(), subscriberId);
      return false;
    }

    subscriber.handleEvent(event);
    consumeRecordJdbcRepository.updateSuccess(event, subscriberId);
    return true;
  }
}
