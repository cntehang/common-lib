package com.tehang.common.utility.event.command;

/**
 * 封装命令执行上下文信息，用于传递前续命令的返回值参数。
 */
public final class CommandExecuteContext {

  private CommandExecuteContext() {
    // do nothing
  }

  /** 前续命令执行结果的线程本地存储 */
  private static final ThreadLocal<CommandReturnValue> commandReturnValueThreadLocal = new ThreadLocal<>();

  /**
   * 获取前续命令的返回值。
   */
  public static CommandReturnValue getPreCommandReturnValue() {
    return commandReturnValueThreadLocal.get();
  }

  /**
   * 设置前续命令的返回值。
   */
  public static void setPreCommandReturnValue(CommandReturnValue value) {
    commandReturnValueThreadLocal.set(value);
  }

  /**
   * 清除当前线程的本地存储。
   */
  public static void clear() {
    commandReturnValueThreadLocal.remove();
  }
}
