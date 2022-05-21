package com.tehang.common.utility.event.publish.DomainEventRecord;

import com.tehang.common.utility.db.jpa.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainEventRecordRepository extends BaseRepository<DomainEventRecord, String> {

}
