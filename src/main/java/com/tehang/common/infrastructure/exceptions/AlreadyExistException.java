package com.tehang.common.infrastructure.exceptions;

/**
 * 资源已经存在.
 */
public class AlreadyExistException extends RuntimeException {

  private static final long serialVersionUID = -1868253660309568973L;

  /**
   * 无參构造器.
   */
  public AlreadyExistException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public AlreadyExistException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public AlreadyExistException(String message, Throwable cause) {
    super(message, cause);
  }
}
