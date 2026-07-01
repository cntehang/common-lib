package com.tehang.common.utility.event.publish;

import com.tehang.common.utility.event.DomainEvent;

/**
 * 领域事件发送到MQ时使用的Message Key.
 */
public final class DomainEventMessageKey {

  private static final String SEPARATOR = "_";

  private DomainEventMessageKey() {
    // do nothing
  }

  /**
   * 从领域事件创建MQ Message Key.
   */
  public static String from(DomainEvent event) {
    return from(event.getEventType(), event.getKey());
  }

  /**
   * 从事件类型和事件业务key创建MQ Message Key.
   */
  public static String from(String eventType, String eventKey) {
    return eventType + SEPARATOR + eventKey;
  }
}
