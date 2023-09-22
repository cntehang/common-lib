package com.tehang.common.utility.event.command;

/**
 * 表示一个命令。
 */
public interface Command {

  /**
   * 获取命令类型
   */
  String getCommandType();

  /**
   * 获取命令的参数类型
   */
  Class<? extends CommandArgs> getArgsClass();

  /**
   * 获取命令的返回参数类型，默认为返回VoidCommandReturnValue。
   */
  default Class<? extends CommandReturnValue> getReturnsClass() {
    return VoidCommandReturnValue.class;
  }

  /**
   * 执行命令。
   */
  CommandReturnValue run(CommandArgs args);
}
