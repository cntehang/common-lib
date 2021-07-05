package com.tehang.common.infrastructure.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * 所有自定义异常的基类.
 */
@Getter
@Setter
public class BaseException extends RuntimeException {

  private static final long serialVersionUID = -7333920888847646059L;

  /**
   * message.
   */
  private String debugMsg;

  /**
   * 无參构造器.
   */
  public BaseException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public BaseException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }

}
