package com.tehang.common.utility.event;

/**
 * 默认的事件参数.
 */
public final class DefaultEvent extends DomainEvent {

  /**
   * 默认构造函数.
   */
  public DefaultEvent() {
    // do nothing
  }

  /**
   * 通过PublishedEventType构造实例，key取uuid.
   */
  public DefaultEvent(String eventType) {
    super(eventType);
  }
}
