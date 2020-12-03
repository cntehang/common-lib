package com.tehang.common.utility.event.cache;

import com.tehang.common.utility.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可动态刷新的内存缓存，直接将指定的数据对象缓存在内存中，并且会订阅相应的事件进行缓存刷新
 */
public abstract class RefreshableObjectCache<T> implements RefreshableCache {

  private static final Logger log = LoggerFactory.getLogger(RefreshableObjectCache.class);

  // 内存中的缓存数据
  private T data = null;
  private final Object lock = new Object();

  /**
   * 获取缓存的数据，初次调用时将引发加载数据的操作
   */
  protected final T getCachedData() {
    synchronized (lock) {
      assertDataLoaded();
      return data;
    }
  }

  private void assertDataLoaded() {
    if (data == null) {
      log.debug("starting load cache data");
      data = getDataActually();
      log.debug("cache data loaded");
    }
  }

  /**
   * 执行实际的加载数据接口，以加载到缓存
   */
  protected abstract T getDataActually();

  /**
   * 缓存数据的加载方式：延迟加载 or 提前加载, 默认为延迟加载
   */
  protected CacheDataFetchType fetchType() {
    return CacheDataFetchType.LAZY;
  }

  /**
   * 刷新缓存
   */
  @Override
  public void refresh(DomainEvent event) {
    synchronized (lock) {
      data = null;

      if (fetchType() == CacheDataFetchType.EAGER) {
        assertDataLoaded();
      }
      log.debug("cache refreshed, cache class: {}", getClass().getSimpleName());
    }
  }
}
