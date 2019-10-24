package com.tehang.common.infrastructure.exceptions;

/**
 * 系统中一些无法处理的异常.
 */
public class SystemErrorException extends RuntimeException {

  /**
   * default constructor.
   */
  public SystemErrorException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public SystemErrorException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public SystemErrorException(String message, Throwable cause) {
    super(message, cause);
  }
}
