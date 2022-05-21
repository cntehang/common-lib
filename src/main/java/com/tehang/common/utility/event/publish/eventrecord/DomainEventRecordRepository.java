package com.tehang.common.utility.event.publish.eventrecord;

import com.tehang.common.utility.db.jpa.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomainEventRecordRepository extends BaseRepository<DomainEventRecord, String> {

  /**
   * 根据状态查询对应的事件记录，按创建时间正序排列，时间早的按在前面。
   */
  List<DomainEventRecord> findByStatusOrderByCreateTimeAsc(DomainEventSendStatus status);
}
