package com.tehang.common.infrastructure.exceptions;

/**
 * 重复操作异常，如重复支付、重复下单等
 */
public class RepetitiveException extends BaseException {

  private static final long serialVersionUID = -4000480866739224547L;

  /**
   * 无參构造器.
   */
  public RepetitiveException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public RepetitiveException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public RepetitiveException(String message, Throwable cause) {
    super(message, cause);
  }
}
