package com.tehang.common.utility.event.mq;

/**
 * MessageProducerException
 */
public class MessageProducerException extends RuntimeException {

  /**
   * 无參构造器.
   */
  public MessageProducerException() {
    super();
    // do nothing.
  }

  /**
   * public constructor with message.
   *
   * @param message the message
   */
  public MessageProducerException(String message) {
    super(message);
  }

  /**
   * public constructor with message and cause.
   *
   * @param message the message
   * @param cause   the cause
   */
  public MessageProducerException(String message, Throwable cause) {
    super(message, cause);
  }
}
