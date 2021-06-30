package com.tehang.common.infrastructure.exceptions;

/**
 * 业务异常，可从此异常派生子类来表达不同的业务异常情况，比如密码错误(此类异常可能不是真正的错误，只是表达一种非正常的情况).
 */
public class BusinessException extends RuntimeException {

  /**
   * 无參构造器.
   */
  public BusinessException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public BusinessException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }
}
