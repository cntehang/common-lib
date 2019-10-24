package com.tehang.common.infrastructure.exceptions;

/**
 * 通用异常，用于某个资源不存在时抛出.
 */
public class NotExistException extends RuntimeException {

  private static final long serialVersionUID = -1868253660309568973L;

  /**
   * 无參构造器.
   */
  public NotExistException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public NotExistException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public NotExistException(String message, Throwable cause) {
    super(message, cause);
  }
}
