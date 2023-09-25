package com.tehang.common.utility.event.command.commandrecord;

import com.tehang.common.utility.time.BjTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.left;

@Repository
@AllArgsConstructor
@Slf4j
public class CommandRecordJdbcRepository {

  private NamedParameterJdbcTemplate jdbcTemplate;

  /** 添加命令列表 */
  public void saveRecords(List<CommandRecord> records) {
    records.forEach(this::saveRecord);
  }

  /** 添加一条命令记录 */
  private void saveRecord(CommandRecord record) {
    String sql = "insert into command_record (id, event_key, command_type, command_args, command_return_value, success, seq_no, create_time, update_time) "
        + "values (:id, :event_key, :command_type, :command_args, :command_return_value, :success, :seq_no, :create_time, :update_time) ";

    Map<String, Object> params = new HashMap<>();
    params.put("id", record.getId());
    params.put("event_key", record.getEventKey());
    params.put("command_type", record.getCommandType());
    params.put("command_args", record.getCommandArgs());
    params.put("command_return_value", record.getCommandReturnValue());
    params.put("success", record.isSuccess());
    params.put("seq_no", record.getSeqNo());
    params.put("create_time", record.getCreateTime().toString());
    params.put("update_time", record.getUpdateTime().toString());

    jdbcTemplate.update(sql, params);
  }

  /**
   * 查询所有的命令记录，按顺序号从低到高排序。
   */
  public List<CommandRecord> findRecordsByEventKey(String eventKey) {
    String sql = String.format("select id, event_key, command_type, command_args, command_return_value, "
        + "success, seq_no, create_time, update_time "
        + "from command_record "
        + "where event_key = '%s' order by seq_no asc ", eventKey);

    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CommandRecord.class));
  }

  /**
   * 命令执行成功后，调用此方法，更新状态为已完成
   */
  public void updateCommandToSuccess(String commandId, String commandReturnValue) {
    String sql = "update command_record "
        + "set success = true, "
        + "    command_return_value = :command_return_value, "
        + "    update_time = :update_time "
        + "where id = :id ";

    Map<String, Object> params = new HashMap<>();
    params.put("id", commandId);
    params.put("command_return_value", commandReturnValue);
    params.put("update_time", BjTime.now().toString());

    jdbcTemplate.update(sql, params);
  }

  /** 命令执行失败后，调用此方法，添加一条命令执行失败的历史记录 */
  public void addCommandHisOnFailed(String commandId, String errorMessage) {
    String sql = "insert into command_record_his (id, command_id, success, error_message, create_time) "
        + "values (:id, :command_id, :success, :error_message, :create_time) ";

    Map<String, Object> params = new HashMap<>();
    params.put("id", UUID.randomUUID().toString());
    params.put("command_id", commandId);
    params.put("success", false);
    params.put("error_message", left(errorMessage, 300));
    params.put("create_time", BjTime.now().toString());

    jdbcTemplate.update(sql, params);
  }
}
