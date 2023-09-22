package com.tehang.common.utility.event.command;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * 服务定位器。根据命令类型查找命令对象。
 */
@AllArgsConstructor
public class CommandLocator {

  private final ApplicationContext applicationContext;

  /**
   * 根据命令类型查找命令对象。
   */
  public Command findCommandEnsured(String commandType) {
    var commands = applicationContext.getBeansOfType(Command.class)
        .values()
        .stream()
        .filter(item -> equalsIgnoreCase(item.getCommandType(), commandType))
        .collect(Collectors.toList());

    if (isEmpty(commands)) {
      throw new SystemErrorException("未找到命令对象, commandType: " + commandType);
    }
    if (commands.size() > 1) {
      throw new SystemErrorException(String.format("找到多个命令对象, commandType: %s, commandNames: %s", commandType, getCommandNames(commands)));
    }

    return commands.get(0);
  }

  private static String getCommandNames(List<Command> commands) {
    return commands.stream()
        .map(item -> item.getClass().getSimpleName())
        .collect(Collectors.joining(","));
  }
}
