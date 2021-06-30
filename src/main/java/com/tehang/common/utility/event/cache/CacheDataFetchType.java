package com.tehang.common.utility.event.cache;

/**
 * 缓存数据的加载模式：延迟加载 or 立即加载.
 */
public enum CacheDataFetchType {

  /**
   * 延迟加载，默认值.
   */
  LAZY,

  /**
   * 提前加载.
   */
  EAGER
}
