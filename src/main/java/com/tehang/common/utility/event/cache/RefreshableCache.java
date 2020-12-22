package com.tehang.common.utility.event.cache;

import com.tehang.common.utility.async.AsyncHelper;
import com.tehang.common.utility.event.DefaultEvent;
import com.tehang.common.utility.event.DomainEvent;
import org.springframework.boot.CommandLineRunner;

/**
 * 可动态刷新的缓存接口: 通过订阅对应的事件对缓存进行刷新
 */
public interface RefreshableCache extends CommandLineRunner {

  /**
   * 刷新缓存
   */
  void refresh(DomainEvent event);

  /**
   * 获取订阅的事件类型
   */
  String subscribedEventType();

  /**
   * 获取订阅事件的参数类型, 默认为DefaultEvent, 子类可以重写，以实现特定的刷新机制
   */
  default Class<? extends DomainEvent> getEventClass() {
    return DefaultEvent.class;
  }

  /**
   * 缓存数据的加载方式：延迟加载 or 提前加载, 默认为延迟加载
   */
  default CacheDataFetchType fetchType() {
    return CacheDataFetchType.LAZY;
  }

  /**
   * 应用初始化时，异步加载缓存
   */
  default void run(String... args) {
    if (fetchType() == CacheDataFetchType.EAGER) {
      AsyncHelper.async(() -> {
        // 为避免影响应用程序的启动，加载缓存采用异步的方式
        refresh(null);
      });
    }
  }

  /**
   * 获取缓存的实例id, 同一缓存类在集群环境中可能有多个实例
   */
  default String getInstanceId() {
    // 取订阅者class名称 + @ + 内存地址的hash值
    return getClass().getSimpleName() + '@' + Integer.toHexString(System.identityHashCode(this));
  }
}
