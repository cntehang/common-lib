package com.tehang.common.utility.event.cache;

import com.tehang.common.utility.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可动态刷新的内存缓存，直接将指定的数据对象缓存在内存中，并且会订阅相应的事件进行缓存刷新.
 */
public abstract class RefreshableObjectCache<T> implements RefreshableCache {

  private static final Logger LOG = LoggerFactory.getLogger(RefreshableObjectCache.class);

  // 内存中的缓存数据
  private T data = null;

  private final Object lock = new Object();

  /**
   * 获取缓存的数据，初次调用时将引发加载数据的操作.
   */
  protected final T getCachedData() {
    synchronized (lock) {
      ensureDataItemsLoaded();
      return data;
    }
  }

  /**
   * 执行实际的加载数据接口，以加载到缓存.
   */
  protected abstract T getDataActually();

  /**
   * 刷新缓存.
   */
  @Override
  public void refresh(DomainEvent event) {
    synchronized (lock) {
      data = null;

      if (fetchType() == CacheDataFetchType.EAGER) {
        ensureDataItemsLoaded();
      }
      LOG.debug("cache refreshed, cache class: {}", getClass().getSimpleName());
    }
  }

  private void ensureDataItemsLoaded() {
    if (data == null) {
      LOG.debug("starting load cache data");
      data = getDataActually();
      LOG.debug("cache data loaded");
    }
  }

}
