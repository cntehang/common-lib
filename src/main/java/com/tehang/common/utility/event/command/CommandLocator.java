package com.tehang.common.utility.event.command;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;

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
    return applicationContext.getBeansOfType(Command.class)
        .values()
        .stream()
        .filter(item -> equalsIgnoreCase(item.getCommandType(), commandType))
        .findFirst()
        .orElseThrow(() -> new SystemErrorException("未找到命令对象, commandType: " + commandType));
  }
}
