package com.tehang.common.utility.event.command;

import com.tehang.common.utility.baseclass.ValueObject;
import lombok.Getter;
import lombok.Setter;

/**
 * 命令信息
 */
@Getter
@Setter
public class CommandInfo extends ValueObject {

  /** 命令类型 */
  private String commandType;

  /** 命令参数信息 */
  private Object args;

  // ----------- 方法 -----------

  public static CommandInfo of(String commandType, Object args) {
    var result = new CommandInfo();
    result.commandType = commandType;
    result.args = args;
    return result;
  }
}
