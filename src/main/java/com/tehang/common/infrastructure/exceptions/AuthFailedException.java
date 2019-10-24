package com.tehang.common.infrastructure.exceptions;

/**
 * 验证权限失败.
 */
public class AuthFailedException extends RuntimeException {

  /**
   * 无參构造器.
   */
  public AuthFailedException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public AuthFailedException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public AuthFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
