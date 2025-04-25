package com.tehang.common.utility.event.publish.eventrecord;

import com.tehang.common.utility.time.BjTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
@Slf4j
public class DomainEventRecordJdbcRepository {

  // 最大发送次数：5次
  private static final int MAX_SEND_TIMES = 5;

  private NamedParameterJdbcTemplate jdbcTemplate;

  /**
   * 添加一条事件记录
   */
  public void add(DomainEventRecord record) {
    String sql = "insert into domain_event_record (id, event_key, event_type, topic, publisher, start_deliver_time, trace_id, body, status, publish_time, count, create_time, update_time) "
        + "values (:id, :event_key, :event_type, :topic, :publisher, :start_deliver_time, :trace_id, :body, :status, :publish_time, :count, :create_time, :update_time) ";

    Map<String, Object> params = new HashMap<>();
    params.put("id", record.getId());
    params.put("event_key", record.getEventKey());
    params.put("event_type", record.getEventType());
    params.put("topic", record.getTopic());
    params.put("publisher", record.getPublisher());
    params.put("start_deliver_time", record.getStartDeliverTime() != null ? record.getStartDeliverTime().toString() : null);
    params.put("trace_id", record.getTraceId());
    params.put("body", record.getBody());
    params.put("status", record.getStatus().toString());
    params.put("publish_time", record.getPublishTime() != null ? record.getPublishTime().toString() : null);
    params.put("count", record.getCount());
    params.put("create_time", record.getCreateTime().toString());
    params.put("update_time", record.getUpdateTime().toString());

    jdbcTemplate.update(sql, params);
  }

  /**
   * 查询所有待发送的事件记录，按创建时间正序排列，时间早的排在前面。
   */
  public List<DomainEventRecord> findAllByWaitSend() {
    String sql = String.format("select id, event_key, event_type, topic, publisher, start_deliver_time, trace_id, body, status, publish_time, count, create_time, update_time "
        + "from domain_event_record where status = '%s' order by create_time ", DomainEventSendStatus.WaitSend);

    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DomainEventRecord.class));
  }

  /**
   * 该事件记录是否处于待发送状态？
   */
  public boolean isWaitSend(String eventRecordId) {
    String sql = String.format("select count(*) > 0 from domain_event_record where id = '%s' and status = '%s'", eventRecordId, DomainEventSendStatus.WaitSend);

    return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new HashMap<>(), Boolean.class));
  }

  /**
   * 发送成功后，更新记录信息
   */
  public void updateOnSendSuccess(DomainEventRecord record) {
    String sql = "update domain_event_record "
        + "set status = :status, "
        + "    count = :count, "
        + "    publish_time = :publish_time, "
        + "    update_time = :update_time "
        + "where id = :id ";

    Map<String, Object> params = new HashMap<>();
    params.put("id", record.getId());
    params.put("status", DomainEventSendStatus.SendSuccess.toString());
    params.put("count", record.getCount() + 1);
    params.put("publish_time", BjTime.now().toString());
    params.put("update_time", BjTime.now().toString());

    jdbcTemplate.update(sql, params);
  }

  /**
   * 发送失败后，更新记录信息
   */
  public void updateOnSendFailed(DomainEventRecord record, String errorMsg) {
    String sql = "update domain_event_record "
        + "set status = :status, "
        + "    count = :count, "
        + "    update_time = :update_time "
        + "where id = :id ";

    int count = record.getCount() + 1;

    Map<String, Object> params = new HashMap<>();
    params.put("id", record.getId());
    params.put("count", count);
    params.put("update_time", BjTime.now().toString());

    if (count >= MAX_SEND_TIMES) {
      // 超过最大发送次数仍然失败，才更新为失败状态
      params.put("status", DomainEventSendStatus.SendFailed.toString());

      log.error("发送事件消息到mq失败, key: {}, eventType: {}, errorMsg: {}", record.getEventKey(), record.getEventType(), errorMsg);
    }
    else {
      // 没超过最大次数，保持待发送状态
      params.put("status", DomainEventSendStatus.WaitSend.toString());
    }

    jdbcTemplate.update(sql, params);
  }
}
