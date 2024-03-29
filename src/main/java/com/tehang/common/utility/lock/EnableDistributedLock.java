package com.tehang.common.utility.lock;

import com.tehang.common.utility.redis.CommonRedisOperator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用分布式锁: DistributedLockFactory.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({DistributedLockFactory.class, CommonRedisOperator.class, DistributedLockHelper.class, WithDistributedLockAspect.class})
public @interface EnableDistributedLock {

}
