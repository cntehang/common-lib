package com.tehang.common.utility.event.cache;

import com.tehang.common.utility.event.DefaultEvent;
import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.subscriber.BroadcastingEventSubscriber;

/**
 * 可动态刷新的缓存接口: 通过订阅对应的事件对缓存进行刷新
 */
public interface RefreshableCache extends BroadcastingEventSubscriber {

  /**
   * 刷新缓存
   */
  void refresh(DomainEvent event);

  @Override
  default Class<? extends DomainEvent> getEventClass() {
    return DefaultEvent.class;
  }

  @Override
  default void handleEvent(DomainEvent event) {
    refresh(event);
  }
}
