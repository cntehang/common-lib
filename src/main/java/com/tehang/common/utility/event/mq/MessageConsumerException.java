package com.tehang.common.utility.event.mq;

/**
 * MessageConsumerException.
 */
public class MessageConsumerException extends RuntimeException {

  /**
   * 无參构造器.
   */
  public MessageConsumerException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public MessageConsumerException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public MessageConsumerException(String message, Throwable cause) {
    super(message, cause);
  }
}
