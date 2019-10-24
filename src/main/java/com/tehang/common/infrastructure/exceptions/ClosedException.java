package com.tehang.common.infrastructure.exceptions;

/**
 * 已经关闭的异常
 */
public class ClosedException extends BaseException {

  private static final long serialVersionUID = -4000480866739224547L;

  /**
   * 无參构造器.
   */
  public ClosedException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public ClosedException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public ClosedException(String message, Throwable cause) {
    super(message, cause);
  }
}
