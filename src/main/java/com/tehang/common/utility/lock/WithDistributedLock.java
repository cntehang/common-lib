package com.tehang.common.utility.lock;

import org.springframework.core.annotation.Order;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在方法上使用分布式锁的注解
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(300)  // 定义一个比较高的优先级
public @interface WithDistributedLock {

  /**
   * 无意义
   */
  String value() default "";

  /**
   * 锁id的附加部分
   */
  String[] keyPieces() default {};

  /**
   * 未获取到锁时，是阻塞线程进行等待，还是抛出异常？默认值为false
   */
  boolean blocked() default true;

  /**
   * 锁的过期时间, 默认值为30秒
   */
  long expiredSeconds() default 30;

}
