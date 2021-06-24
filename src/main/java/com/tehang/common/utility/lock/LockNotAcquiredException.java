package com.tehang.common.utility.lock;

/**
 * 未获取到分布式锁引发的异常
 */
public class LockNotAcquiredException extends RuntimeException {

  /**
   * 无參构造器.
   */
  public LockNotAcquiredException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public LockNotAcquiredException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public LockNotAcquiredException(String message, Throwable cause) {
    super(message, cause);
  }
}
