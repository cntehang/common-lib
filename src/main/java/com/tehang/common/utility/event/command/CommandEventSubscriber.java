package com.tehang.common.utility.event.command;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.command.commandrecord.CommandRecord;
import com.tehang.common.utility.event.command.commandrecord.CommandRecordJdbcRepository;
import com.tehang.common.utility.event.subscriber.ClusteringEventSubscriber;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * 【命令事件】事件订阅者
 */
@Slf4j
@AllArgsConstructor
public class CommandEventSubscriber implements ClusteringEventSubscriber {

  private final CommandLocator commandLocator;
  private final CommandEventTypeHolder eventTypeHolder;
  private final CommandRecordJdbcRepository commandRecordJdbcRepository;

  @Override
  public String subscribedEventType() {
    return eventTypeHolder.getEventType();
  }

  @Override
  public Class<? extends DomainEvent> getEventClass() {
    return CommandEvent.class;
  }

  /**
   * 【命令事件】事件处理
   */
  @Override
  public void handleEvent(DomainEvent event) {
    // 获取事件参数
    var commandEvent = (CommandEvent)event;
    log.debug("Enter handleCommandEvent, commandType: {}, args: {}", commandEvent.getCommandType(), commandEvent.getArgs());

    if (equalsIgnoreCase(commandEvent.getCommandType(), CommandManager.AGGREGATE_COMMAND_TYPE)) {
      // 执行聚合命令：多个命令按顺序执行
      executeAggregateCommand(commandEvent);
    }
    else {
      // 执行简单命令
      executeSimpleCommand(commandEvent);
    }

    log.debug("Exit handleCommandEvent, commandType: {}, args: {}", commandEvent.getCommandType(), commandEvent.getArgs());
  }

  private void executeSimpleCommand(CommandEvent commandEvent) {
    // 查找命令对象
    Command command = commandLocator.findCommandEnsured(commandEvent.getCommandType());

    // 解析命令参数
    var args = JsonUtils.toClass(commandEvent.getArgs(), command.getArgsClass());

    // 运行命令
    command.run(args);
  }

  /** 执行聚合命令，其包含多个子命令，按顺序执行这些子命令。*/
  private void executeAggregateCommand(CommandEvent commandEvent) {
    // 查找命令对象列表, 按顺序号从低到高排序
    List<CommandRecord> commandRecords = commandRecordJdbcRepository.findRecordsByEventKey(commandEvent.getKey());
    if (isEmpty(commandRecords)) {
      throw new SystemErrorException("commandRecords not found, event: " + JsonUtils.toJson(commandEvent));
    }

    // 前续命令的返回值
    CommandReturnValue prCommandReturnValue = null;

    // 依次执行每个命令
    for (var commandRecord : commandRecords) {
      // 查找命令对象
      Command command = commandLocator.findCommandEnsured(commandRecord.getCommandType());

      if (commandRecord.isSuccess()) {
        // 如果该命令已执行成功，取得该命令的执行结果，继续下一条命令
        prCommandReturnValue = JsonUtils.toClass(commandRecord.getCommandReturnValue(), command.getReturnsClass());
      }
      else {
        // 如果该命令未执行成功，先设置上下文参数，再执行该命令
        CommandExecuteContext.setPreCommandReturnValue(prCommandReturnValue);

        // 运行子命令, 并获取返回值
        prCommandReturnValue = executeSubCommand(commandRecord, command);
      }
    }

    // 执行完成后，清除上下文参数
    CommandExecuteContext.clear();
  }

  /** 运行子命令, 并获取返回值 */
  private CommandReturnValue executeSubCommand(CommandRecord commandRecord, Command command) {
    log.debug("Enter executeSubCommand, commandId: {}", commandRecord.getId());

    CommandReturnValue result;
    try {
      // 解析命令参数
      CommandArgs args = JsonUtils.toClass(commandRecord.getCommandArgs(), command.getArgsClass());

      // 执行命令
      result = command.run(args);
    }
    catch (Exception ex) {
      log.warn("executeSubCommand failed, commandId: {}, errorMessage: {}", commandRecord.getId(), ex.getMessage(), ex);

      // 记录执行失败日志
      addCommandFailedHis(commandRecord.getId(), ex.getMessage());

      // 清除上下文参数
      CommandExecuteContext.clear();

      // 抛出异常，不再继续执行命令，依赖mq的重试机制，下次再执行该命令
      throw ex;
    }

    // 更新命令状态为执行成功
    updateCommandToSuccess(commandRecord.getId(), result);
    return result;
  }

  /** 命令执行成功后，调用此方法，更新状态为已完成 */
  private void updateCommandToSuccess(String commandId, CommandReturnValue commandReturnValue) {
    commandRecordJdbcRepository.updateCommandToSuccess(commandId, JsonUtils.toJson(commandReturnValue));
  }

  /** 命令执行失败后，调用此方法，记录失败历史 */
  private void addCommandFailedHis(String commandId, String errorMessage) {
    commandRecordJdbcRepository.addCommandHisOnFailed(commandId, errorMessage);
  }
}
