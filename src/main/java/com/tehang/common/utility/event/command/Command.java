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
   * 执行命令。
   */
  void run(CommandArgs args);
}
