package com.tehang.common.infrastructure.exceptions;

/**
 * 可以重试的异常。
 */
public class RepeatableException extends RuntimeException {

  /**
   * default constructor.
   */
  public RepeatableException() {
    super();
    // do nothing.
  }

  public RepeatableException(String message) {
    super(message);
  }

  public RepeatableException(String message, Throwable cause) {
    super(message, cause);
  }
}
