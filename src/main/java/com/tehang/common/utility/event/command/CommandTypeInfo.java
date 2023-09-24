package com.tehang.common.utility.event.command;

import lombok.Data;

/**
 * 命令参数的类型信息
 */
@Data
public class CommandTypeInfo {

  /** 命令请求参数类型 */
  private Class<?> argsClass;

  /** 命令返回值类型 */
  private Class<?> returnClass;

  // ---------- 方法 -----------

  public static CommandTypeInfo of(Class<?> argsClass, Class<?> returnClass) {
    var result = new CommandTypeInfo();
    result.argsClass = argsClass;
    result.returnClass = returnClass;
    return result;
  }
}
