package com.tehang.common.utility.lock;

import org.springframework.core.annotation.Order;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法调用时使用分布式锁。
 * eg:
 * Locked(): 锁定整个方法，lockKey = 包名 + 类名 + 方法名;
 * Locked(keyPieces = {"Prefix", "#param1", "#param2.field1"}): 锁定时考虑参数值, lockKey = 包名 + 类名 + 方法名 + keyPieces;
 * Locked(blocked = true): 获取锁失败时阻塞线程，直到超时, blocked默认值为false。
 * Locked(expiredSeconds = 60): 锁的过期时间设置为60秒。
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(300)  // 定义一个比较高的优先级
public @interface Locked {

  /**
   * 无意义
   */
  String value() default "";

  /**
   * lockKey的附加部分
   */
  String[] keyPieces() default {};

  /**
   * 未获取到锁时，是阻塞线程进行等待，还是直接抛出异常？默认值为false
   */
  boolean blocked() default false;

  /**
   * 锁的过期时间, 默认值为30秒
   */
  long expiredSeconds() default 30;

}
