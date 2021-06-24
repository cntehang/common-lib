package com.tehang.common.utility.lock;

/**
 * 获取分布式锁超时引发的异常
 */
public class LockTimeoutException extends RuntimeException {

  /**
   * 无參构造器.
   */
  public LockTimeoutException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public LockTimeoutException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public LockTimeoutException(String message, Throwable cause) {
    super(message, cause);
  }
}
