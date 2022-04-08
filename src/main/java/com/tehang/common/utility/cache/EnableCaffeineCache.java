package com.tehang.common.utility.cache;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用Caffeine缓存组件。
 *
 * 使用如下：
 * 1. Application.java加上注解 @EnableCaffeineCache；
 * 2. 在需要缓存的方法加上注解Cacheable。
 *
 * 使用示例：
 *  '@Cacheable(cacheNames = "cacheName", cacheManager = "cacheManagerOneMinute")
 *   public String getCacheData() { }
 *
 *  支持以下cacheManager:
 *    cacheManagerOneMinute, cacheManagerFiveMinutes, cacheManagerTenMinutes,
 *    cacheManagerOneHour, cacheManagerFiveHours, cacheManagerTenHours。
 *    如果不指定cacheManager参数，则默认的cacheManager为缓存10分钟。
 *
 *  也可以自定义cacheManager, 如下所示：
 *  '@Cacheable(cacheNames = "cacheName", cacheManager = "cacheManagerFiveSeconds")
 *   public String getCacheData() { }
 *
 *  // 自定义的CacheManager定义
 *  '@Bean
 *   public CacheManager cacheManagerFiveSeconds() {
 *     return CaffeineCacheConfig.createCacheManager(5, TimeUnit.SECONDS);
 *   }
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(CaffeineCacheConfig.class)
public @interface EnableCaffeineCache {

}
