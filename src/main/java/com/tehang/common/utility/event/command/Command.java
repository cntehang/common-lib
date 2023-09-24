package com.tehang.common.utility.event.command;

/**
 * 表示一个命令。
 */
public interface Command<T, R> {

  /**
   * 获取命令类型
   */
  String getCommandType();

  /**
   * 执行命令。
   */
  R run(T args);
}
