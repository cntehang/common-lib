package com.tehang.common.utility.event.command;

import lombok.Getter;
import lombok.Setter;

/**
 * 用来获取CommandEventType的bean。在启动时读取EnableCommand中的值，并动态注册。
 */
@Getter
@Setter
public class CommandEventTypeHolder {

  /**
   * 命令模式使用的事件类型名。
   */
  private String eventType;
}
