package com.tehang.common.utility.event.command;

import com.tehang.common.utility.event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 【命令事件】，用来封装异步命令的事件。
 */
@Getter
@Setter
@NoArgsConstructor
public class CommandEvent extends DomainEvent {

  /** 命令类型 */
  private String commandType;

  /** 命令参数对象的json表示 */
  private String args;

  // ------------ 方法 -----------

  public CommandEvent(String eventType, String commandType, String args) {
    super(eventType);

    this.commandType = commandType;
    this.args = args;
  }
}
