package com.tehang.common.utility.event.cache;

import com.tehang.common.utility.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 可动态刷新的内存缓存，缓存采用HashMap数据结构，并且会订阅相应的事件进行缓存刷新.
 */
public abstract class RefreshableMapCache<T> implements RefreshableCache {

  private static final Logger LOG = LoggerFactory.getLogger(RefreshableMapCache.class);

  // 内存中的缓存数据，Map类型
  private Map<String, T> cacheItemsMap = null;

  private final Object lock = new Object();

  /**
   * 根据key值获取缓存的数据项.
   */
  public final T getDataItem(String key) {
    synchronized (lock) {
      ensureDataItemsLoaded();

      var result = cacheItemsMap.get(key);
      LOG.trace("Exit getItem, item: {}", result);
      return result;
    }
  }

  /**
   * 获取所有缓存的数据项.
   */
  public final List<T> getAllDataItems() {
    synchronized (lock) {
      ensureDataItemsLoaded();
      return new ArrayList<>(cacheItemsMap.values());
    }
  }

  /**
   * 获取缓存数据的key值.
   */
  protected abstract String getCacheKey(T data);

  /**
   * 获取实际数据的方法，缓存类调用此方法加载缓存数据.
   */
  protected abstract List<T> getDataItemsActually();

  /**
   * 刷新缓存.
   */
  @Override
  public void refresh(DomainEvent event) {
    synchronized (lock) {
      cacheItemsMap = null;

      if (fetchType() == CacheDataFetchType.EAGER) {
        ensureDataItemsLoaded();
      }
      LOG.trace("cache refreshed, cache class: {}", this.getClass().getSimpleName());
    }
  }

  private void ensureDataItemsLoaded() {
    if (cacheItemsMap == null) {
      LOG.debug("starting load cache data");

      List<T> dataItems = getDataItemsActually();
      cacheItemsMap = dataItems.stream().collect(Collectors.toMap(this::getCacheKey, Function.identity(), (v1, v2) -> v1));

      LOG.debug("cache data loaded, size: {}", dataItems.size());
    }
  }

}
