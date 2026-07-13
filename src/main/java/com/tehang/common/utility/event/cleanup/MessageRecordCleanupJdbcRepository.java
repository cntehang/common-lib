package com.tehang.common.utility.event.cleanup;

import com.tehang.common.utility.time.BjTime;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息记录清理仓储.
 */
@Repository
@AllArgsConstructor
public class MessageRecordCleanupJdbcRepository {

  private static final String CUTOFF_TIME = "cutoff_time";

  private static final String BATCH_SIZE = "batch_size";

  private static final String EVENT_KEYS = "event_keys";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  /** 分批删除已完成的过期事件记录. */
  public int deleteEventRecordsBefore(BjTime cutoffTime, int batchSize) {
    String sql = "delete from domain_event_record "
        + "where status in ('SendSuccess', 'SendFailed') "
        + "  and create_time < :cutoff_time "
        + "order by create_time "
        + "limit :batch_size ";
    return jdbcTemplate.update(sql, getBatchParams(cutoffTime, batchSize));
  }

  /** 分批删除已成功的过期消费记录. */
  public int deleteConsumeRecordsBefore(BjTime cutoffTime, int batchSize) {
    String sql = "delete from domain_event_consume_record "
        + "where status = 'Success' "
        + "  and create_time < :cutoff_time "
        + "order by create_time "
        + "limit :batch_size ";
    return jdbcTemplate.update(sql, getBatchParams(cutoffTime, batchSize));
  }

  /** 分批删除过期命令历史. */
  public int deleteCommandHistoryBefore(BjTime cutoffTime, int batchSize) {
    String sql = "delete from command_record_his "
        + "where create_time < :cutoff_time "
        + "order by create_time "
        + "limit :batch_size ";
    return jdbcTemplate.update(sql, getBatchParams(cutoffTime, batchSize));
  }

  /** 查询可整组清理的过期命令事件key. */
  public List<String> findExpiredCommandEventKeysBefore(BjTime cutoffTime, int batchSize) {
    String sql = "select distinct expired.event_key "
        + "from command_record expired "
        + "where expired.create_time < :cutoff_time "
        + "  and not exists ("
        + "    select 1 from command_record retained "
        + "    where retained.event_key = expired.event_key "
        + "      and retained.create_time >= :cutoff_time"
        + "  ) "
        + "order by expired.event_key "
        + "limit :batch_size ";
    return jdbcTemplate.queryForList(sql, getBatchParams(cutoffTime, batchSize), String.class);
  }

  /** 删除命令组关联的所有历史. */
  public int deleteCommandHistoryByEventKeys(List<String> eventKeys) {
    String sql = "delete his from command_record_his his "
        + "inner join command_record cmd on cmd.id = his.command_id "
        + "where cmd.event_key in (:event_keys) ";
    return jdbcTemplate.update(sql, Map.of(EVENT_KEYS, eventKeys));
  }

  /** 按事件key整组删除命令记录. */
  public int deleteCommandRecordsByEventKeys(List<String> eventKeys) {
    String sql = "delete from command_record where event_key in (:event_keys) ";
    return jdbcTemplate.update(sql, Map.of(EVENT_KEYS, eventKeys));
  }

  /** 统计超期待发送事件数量. */
  public long countExpiredWaitSendRecords(BjTime cutoffTime) {
    String sql = "select count(*) from domain_event_record "
        + "where status = 'WaitSend' and create_time < :cutoff_time ";
    return queryForCount(sql, cutoffTime);
  }

  /** 统计超期处理中消费记录数量. */
  public long countExpiredProcessingRecords(BjTime cutoffTime) {
    String sql = "select count(*) from domain_event_consume_record "
        + "where status = 'Processing' and create_time < :cutoff_time ";
    return queryForCount(sql, cutoffTime);
  }

  private long queryForCount(String sql, BjTime cutoffTime) {
    Long result = jdbcTemplate.queryForObject(sql, getCutoffTimeParam(cutoffTime), Long.class);
    return result == null ? 0L : result;
  }

  private static Map<String, Object> getBatchParams(BjTime cutoffTime, int batchSize) {
    Map<String, Object> params = getCutoffTimeParam(cutoffTime);
    params.put(BATCH_SIZE, batchSize);
    return params;
  }

  private static Map<String, Object> getCutoffTimeParam(BjTime cutoffTime) {
    Map<String, Object> params = new HashMap<>();
    params.put(CUTOFF_TIME, cutoffTime.toString());
    return params;
  }
}
