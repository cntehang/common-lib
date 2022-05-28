package com.tehang.common.utility.event.eventrecord;

import com.tehang.common.utility.db.jpa.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 领域事件记录仓储
 */
@Repository
public interface DomainEventRecordRepository extends BaseRepository<DomainEventRecord, String> {

  /**
   * 根据事件的key获取事件记录
   */
  DomainEventRecord findByEventKey(String eventKey);
}
