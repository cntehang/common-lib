package com.tehang.common.utility.event.consume;

/**
 * 领域事件消费状态.
 */
public enum DomainEventConsumeStatus {

  /**
   * 消费事务内的占位状态.
   */
  Processing,

  /**
   * 已消费成功.
   */
  Success
}
