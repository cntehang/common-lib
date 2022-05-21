package com.tehang.common.utility.event.publish.DomainEventRecord;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 领域事件的发送状态
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DomainEventSendStatus {

  /**
   * 刚创建事件记录时，为待发送状态。
   */
  WaitSend("待发送"),

  /**
   * 发送到mq以后，更新状态为发送成功。
   */
  SendSuccess("发送成功"),

  /**
   * 发送到mq失败后，更新状态为发送失败。(指经过重试，仍然失败的状态。)
   */
  SendFailed("发送失败");

  private String description;
}
