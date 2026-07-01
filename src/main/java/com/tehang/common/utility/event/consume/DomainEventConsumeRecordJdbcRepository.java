package com.tehang.common.utility.event.consume;

import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.time.BjTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 领域事件消费记录仓储.
 */
@Repository
@AllArgsConstructor
@Slf4j
public class DomainEventConsumeRecordJdbcRepository {

  private static final String INSERT_SQL = "insert into domain_event_consume_record "
      + "(id, event_key, event_type, subscriber_id, status, consume_time, error_message, create_time, update_time) "
      + "values (:id, :event_key, :event_type, :subscriber_id, :status, "
      + ":consume_time, :error_message, :create_time, :update_time) ";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  /**
   * 插入消费处理中记录.
   */
  public boolean insertProcessing(DomainEvent event, String subscriberId) {
    DomainEventConsumeRecord record = DomainEventConsumeRecord.createProcessing(event, subscriberId);
    try {
      jdbcTemplate.update(INSERT_SQL, getInsertParams(record));
      return true;
    }
    catch (DuplicateKeyException ex) {
      log.warn("事件已消费或正在消费, key: {}, eventType: {}, subscriberId: {}",
          event.getKey(), event.getEventType(), subscriberId);
      return false;
    }
  }

  /**
   * 更新消费记录为成功状态.
   */
  public void updateSuccess(DomainEvent event, String subscriberId) {
    final String sql = "update domain_event_consume_record "
        + "set status = :status, "
        + "    consume_time = :consume_time, "
        + "    update_time = :update_time "
        + "where event_key = :event_key "
        + "  and event_type = :event_type "
        + "  and subscriber_id = :subscriber_id ";

    BjTime now = BjTime.now();
    Map<String, Object> params = new HashMap<>();
    params.put("event_key", event.getKey());
    params.put("event_type", event.getEventType());
    params.put("subscriber_id", subscriberId);
    params.put("status", DomainEventConsumeStatus.Success.toString());
    params.put("consume_time", now.toString());
    params.put("update_time", now.toString());

    jdbcTemplate.update(sql, params);
  }

  private Map<String, Object> getInsertParams(DomainEventConsumeRecord record) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", record.getId());
    params.put("event_key", record.getEventKey());
    params.put("event_type", record.getEventType());
    params.put("subscriber_id", record.getSubscriberId());
    params.put("status", record.getStatus().toString());
    params.put("consume_time", record.getConsumeTime() != null ? record.getConsumeTime().toString() : null);
    params.put("error_message", record.getErrorMessage());
    params.put("create_time", record.getCreateTime().toString());
    params.put("update_time", record.getUpdateTime().toString());
    return params;
  }
}
