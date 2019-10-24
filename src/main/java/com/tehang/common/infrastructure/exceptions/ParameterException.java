package com.tehang.common.infrastructure.exceptions;

/**
 * 参数异常
 */
public class ParameterException extends RuntimeException {

  private static final long serialVersionUID = -8521049383304875407L;

  /**
   * 无參构造器.
   */
  public ParameterException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public ParameterException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public ParameterException(String message, Throwable cause) {
    super(message, cause);
  }
}
