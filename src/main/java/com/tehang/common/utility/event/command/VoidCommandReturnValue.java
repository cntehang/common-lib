package com.tehang.common.utility.event.command;

/**
 * 无参的命令返回参数。
 */
public class VoidCommandReturnValue extends CommandReturnValue {

  public static VoidCommandReturnValue of() {
    return new VoidCommandReturnValue();
  }
}
