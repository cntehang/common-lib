package com.tehang.common.utility.event.cache;

import com.tehang.common.utility.event.DefaultEvent;
import com.tehang.common.utility.event.DomainEvent;

/**
 * 可动态刷新的缓存接口: 通过订阅对应的事件对缓存进行刷新
 */
public interface RefreshableCache {

  /**
   * 刷新缓存
   */
  void refresh(DomainEvent event);

  /**
   * 获取订阅的事件类型
   */
  String[] subscribedEventTypes();

  /**
   * 获取订阅事件的参数类型, 默认为DefaultEvent, 子类可以重写，以实现特定的刷新机制
   */
  default Class<? extends DomainEvent> getEventClass() {
    return DefaultEvent.class;
  }

  /**
   * 获取缓存的实例id, 同一缓存类在集群环境中可能有多个实例
   */
  default String getInstanceId() {
    // 取订阅者class名称 + @ + 内存地址的hash值
    return getClass().getSimpleName() + '@' + Integer.toHexString(System.identityHashCode(this));
  }
}
