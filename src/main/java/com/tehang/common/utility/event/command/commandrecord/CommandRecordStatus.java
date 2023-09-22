package com.tehang.common.utility.event.command.commandrecord;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 命令记录的处理状态
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommandRecordStatus {

  /**
   * 刚创建命令记录时，待处理。
   */
  WaitProcess("待处理"),

  /**
   * 命令已执行成功。
   */
  Success("执行成功");

  private final String description;
}
