package com.tehang.common.utility.event.mq;

/**
 * 消息的消费状态
 */
enum EventConsumeStatus {

  /**
   * 消费中: 正在处理消息的过程中
   */
  Consuming,

  /**
   * 消费成功
   */
  ConsumeSucceeded,

  /**
   * 消费失败
   */
  ConsumeFailed;
}
