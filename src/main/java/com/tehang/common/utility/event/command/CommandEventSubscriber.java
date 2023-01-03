package com.tehang.common.utility.event.command;

import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.subscriber.ClusteringEventSubscriber;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 【命令事件】事件订阅者
 */
@Slf4j
@AllArgsConstructor
public class CommandEventSubscriber implements ClusteringEventSubscriber {

  private final CommandLocator commandLocator;
  private final CommandEventTypeHolder eventTypeHolder;

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

    // 查找命令对象
    Command command = commandLocator.findCommand(commandEvent.getCommandType());

    // 解析命令参数
    var args = JsonUtils.toClass(commandEvent.getArgs(), command.getArgsClass());

    // 运行命令
    command.run(args);

    log.debug("Exit handleCommandEvent, commandType: {}, args: {}", commandEvent.getCommandType(), commandEvent.getArgs());
  }
}
