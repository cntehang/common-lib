package com.tehang.common.utility.event.command;

import com.aliyun.openservices.shade.com.google.common.collect.Lists;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.event.command.commandrecord.CommandRecord;
import com.tehang.common.utility.event.command.commandrecord.CommandRecordJdbcRepository;
import com.tehang.common.utility.event.publish.TransactionalEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * 命令管理器，用来生成任务。
 */
@Slf4j
@AllArgsConstructor
public class CommandManager {

  /** 聚合命令的命令类型 */
  public static final String AGGREGATE_COMMAND_TYPE = "AGGREGATE_COMMAND_TYPE";

  private final TransactionalEventPublisher eventPublisher;
  private final CommandEventTypeHolder eventTypeHolder;
  private final CommandRecordJdbcRepository commandRecordJdbcRepository;

  /**
   * 添加一个命令，并发布事件，以触发异步执行。
   */
  public void queueCommand(String commandType, Object args) {
    log.debug("Enter queueCommand, commandType: {}, args: {}", commandType, args);

    eventPublisher.publish(new CommandEvent(eventTypeHolder.getEventType(), commandType, JsonUtils.toJson(args)));

    log.debug("Exit queueCommand, commandType: {}, args: {}", commandType, args);
  }

  /**
   * 异步执行多个命令，并按顺序执行。
   */
  public void queueCommands(CommandInfo... commands) {
    queueCommands(Lists.newArrayList(commands));
  }

  /**
   * 异步执行多个命令，并按顺序执行。
   */
  public void queueCommands(List<CommandInfo> commands) {
    if (log.isDebugEnabled()) {
      log.debug("Enter queueCommands, commands: {}", JsonUtils.toJson(commands));
    }

    if (isEmpty(commands)) {
      return;
    }

    // 创建命令事件
    CommandEvent commandEvent = new CommandEvent(eventTypeHolder.getEventType(), AGGREGATE_COMMAND_TYPE, null);
    
    // 创建命令列表，并保存
    List<CommandRecord> commandRecords = createCommandRecords(commands, commandEvent.getKey());
    commandRecordJdbcRepository.saveRecords(commandRecords);

    // 发布命令事件
    eventPublisher.publish(commandEvent);

    log.debug("Exit queueCommands");
  }

  private List<CommandRecord> createCommandRecords(List<CommandInfo> commands, String eventKey) {
    List<CommandRecord> records = new ArrayList<>();

    for (int i = 0; i < commands.size(); i++) {
      CommandInfo item = commands.get(i);
      records.add(createCommandRecord(item, eventKey, i + 1));
    }
    return records;
  }

  private CommandRecord createCommandRecord(CommandInfo commandInfo, String eventKey, int seqNo) {
    var record = new CommandRecord();
    record.setId(UUID.randomUUID().toString());
    record.setEventKey(eventKey);
    record.setCommandType(commandInfo.getCommandType());
    record.setCommandArgs(JsonUtils.toJson(commandInfo.getArgs()));
    record.setCommandReturnValue(null);
    record.setSuccess(false);
    record.setSeqNo(seqNo);

    record.resetCreateAndUpdateTimeToNow();
    return record;
  }
}
