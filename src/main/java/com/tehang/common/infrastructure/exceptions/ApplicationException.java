package com.tehang.common.infrastructure.exceptions;

import lombok.Getter;

/**
 * 应用层异常，所有的应用层，只抛出这一个异常
 */
@Getter
public class ApplicationException extends BaseException {

  private static final long serialVersionUID = 4297291666829904747L;
  int code;

  /**
   * 无參构造器.
   */
  public ApplicationException() {
    super("application exception");
    // do nothing.
  }

  /**
   * constructor with code.
   *
   * @param code
   */
  public ApplicationException(int code) {
    super();
    this.code = code;
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public ApplicationException(String message) {
    super(message);
  }


  /**
   * public constructor with code & message.
   *
   * @param code
   * @param message
   */
  public ApplicationException(int code, String message) {
    super(message);
    this.code = code;
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }


  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public ApplicationException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

}
