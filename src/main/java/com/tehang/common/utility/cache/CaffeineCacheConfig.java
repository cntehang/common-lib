package com.tehang.common.utility.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * CaffeineCache的相关配置
 */
@Configuration
@EnableCaching
public class CaffeineCacheConfig {

  /**
   * CacheManager, 缓存1分钟
   */
  @Bean
  public CacheManager cacheManagerOneMinute() {
    return createCacheManager(1, TimeUnit.MINUTES);
  }

  /**
   * CacheManager, 缓存5分钟
   */
  @Bean
  public CacheManager cacheManagerFiveMinutes() {
    return createCacheManager(5, TimeUnit.MINUTES);
  }

  /**
   * CacheManager, 缓存10分钟
   */
  @Bean
  public CacheManager cacheManagerTenMinutes() {
    return createCacheManager(10, TimeUnit.MINUTES);
  }

  /**
   * CacheManager, 缓存1小时
   */
  @Bean
  public CacheManager cacheManagerOneHour() {
    return createCacheManager(1, TimeUnit.HOURS);
  }

  /**
   * CacheManager, 缓存5小时
   */
  @Bean
  public CacheManager cacheManagerFiveHours() {
    return createCacheManager(5, TimeUnit.HOURS);
  }

  /**
   * cacheManagerOneHour, 缓存10小时
   */
  @Bean
  public CacheManager cacheManagerTenHours() {
    return createCacheManager(10, TimeUnit.HOURS);
  }

  /**
   * 默认的CacheManager, 缓存10分钟
   */
  @Bean
  @Primary
  public CacheManager cacheManager() {
    return createCacheManager(10, TimeUnit.MINUTES);
  }

  /**
   * 根据过期时长创建CacheManager
   */
  public static CacheManager createCacheManager(long duration, TimeUnit unit) {
    var cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(duration, unit));
    return cacheManager;
  }
}
