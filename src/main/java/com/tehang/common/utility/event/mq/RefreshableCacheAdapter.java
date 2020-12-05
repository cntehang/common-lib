package com.tehang.common.utility.event.mq;

import com.tehang.common.utility.event.DomainEvent;
import com.tehang.common.utility.event.cache.RefreshableCache;
import com.tehang.common.utility.event.subscriber.BroadcastingEventSubscriber;

/**
 * 缓存适配器，用来将Refreshable接口转换为BroadcastingEventSubscriber接口。
 */
public class RefreshableCacheAdapter implements BroadcastingEventSubscriber {

  private RefreshableCache refreshableCache;

  public RefreshableCacheAdapter(RefreshableCache refreshableCache) {
    this.refreshableCache = refreshableCache;
  }

  @Override
  public String subscribedEventType() {
    return refreshableCache.subscribedEventType();
  }

  @Override
  public Class<? extends DomainEvent> getEventClass() {
    return refreshableCache.getEventClass();
  }

  @Override
  public void handleEvent(DomainEvent event) {
    // 接收到事件通知时，调用缓存的refresh方法
    refreshableCache.refresh(event);
  }

  @Override
  public String getInstanceId() {
    return refreshableCache.getInstanceId();
  }
}
