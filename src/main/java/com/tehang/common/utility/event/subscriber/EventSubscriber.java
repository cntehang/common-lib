package com.tehang.common.utility.event.subscriber;

import com.tehang.common.utility.event.DomainEvent;

/**
 * 事件订阅者的root接口
 */
public interface EventSubscriber {

  /**
   * 获取订阅的事件类型数组，支持一个订阅者订阅多个事件类型，但需要保证事件参数相同
   */
  String[] subscribedEventTypes();

  /**
   * 获取订阅事件的参数类型
   */
  Class<? extends DomainEvent> getEventClass();

  /**
   * 处理事件
   */
  void handleEvent(DomainEvent event);

  /**
   * 获取订阅者的实例id, 同一订阅者在集群环境中可能有多个实例
   */
  default String getInstanceId() {
    // 取订阅者class名称 + @ + 内存地址的hash值
    return getClass().getSimpleName() + '@' + Integer.toHexString(System.identityHashCode(this));
  }
}
