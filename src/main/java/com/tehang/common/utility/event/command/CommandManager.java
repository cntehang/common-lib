package com.tehang.common.utility.event.command;

import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.event.publish.TransactionalEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 命令管理器，用来生成任务。
 */
@Slf4j
@AllArgsConstructor
public class CommandManager {

  private final TransactionalEventPublisher eventPublisher;
  private final CommandEventTypeHolder eventTypeHolder;

  /**
   * 添加一个命令，并发布事件，以触发异步执行。
   */
  public void queueCommand(String commandType, CommandArgs args) {
    log.debug("Enter queueCommand, commandType: {}, args: {}", commandType, args);

    eventPublisher.publish(new CommandEvent(eventTypeHolder.getEventType(), commandType, JsonUtils.toJson(args)));

    log.debug("Exit queueCommand, commandType: {}, args: {}", commandType, args);
  }
}
