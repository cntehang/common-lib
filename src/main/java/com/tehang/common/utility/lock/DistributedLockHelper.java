package com.tehang.common.utility.lock;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 提供使用分布式锁的辅助方法
 *
 * 使用方法：
 * 1. 注入:
 * DistributedLockHelper lockHelper;
 * 2. 使用：
 * lockHelper.withLock("lockId", () -> {
 *   // do something in locked
 * }
 */
@Component
@AllArgsConstructor
public class DistributedLockHelper {

  private static final long DEFAULT_EXPIRED_SECONDS = 30;
  private final DistributedLockFactory lockFactory;

  /**
   * 在分布式锁中执行方法
   */
  public void withLock(String lockId, Runnable runnable) {
    withLock(lockId, false, DEFAULT_EXPIRED_SECONDS, runnable);
  }

  /**
   * 在分布式锁中执行方法，并指定blocked参数
   */
  public void withLock(String lockId, boolean blocked, Runnable runnable) {
    withLock(lockId, blocked, DEFAULT_EXPIRED_SECONDS, runnable);
  }

  /**
   * 在分布式锁中执行方法，并指定blocked, expiredSeconds参数
   */
  public void withLock(String lockId, boolean blocked, long expiredSeconds, Runnable runnable) {
    try (var ignored = lockFactory.acquireLock(lockId, blocked, expiredSeconds * 1000)) {
      runnable.run();
    }
  }

  /**
   * 在分布式锁中执行方法，并指定是否阻塞、锁过期时间和获取等待时间.
   *
   * @param lockId 锁id, 不能为空
   * @param blocked 获取不到锁时是否阻塞等待
   * @param expiredSeconds 锁的过期时间，单位为秒
   * @param acquireWaitSeconds 获取锁的等待时间，单位为秒，不能为负数
   * @param runnable 获取锁后执行的方法
   */
  public void withLock(
      String lockId,
      boolean blocked,
      long expiredSeconds,
      long acquireWaitSeconds,
      Runnable runnable) {
    try (var ignored = lockFactory.acquireLock(
        lockId,
        blocked,
        TimeUnit.SECONDS.toMillis(expiredSeconds),
        TimeUnit.SECONDS.toMillis(acquireWaitSeconds))) {
      runnable.run();
    }
  }

  /**
   * 在分布式锁中执行方法
   */
  public <T> T withLock(String lockId, Supplier<T> supplier) {
    return withLock(lockId, false, DEFAULT_EXPIRED_SECONDS, supplier);
  }

  /**
   * 在分布式锁中执行方法，并指定blocked参数
   */
  public <T> T withLock(String lockId, boolean blocked, Supplier<T> supplier) {
    return withLock(lockId, blocked, DEFAULT_EXPIRED_SECONDS, supplier);
  }

  /**
   * 在分布式锁中执行方法，并指定blocked, expiredSeconds参数
   */
  public <T> T withLock(String lockId, boolean blocked, long expiredSeconds, Supplier<T> supplier) {
    try (var ignored = lockFactory.acquireLock(lockId, blocked, expiredSeconds * 1000)) {
      return supplier.get();
    }
  }

  /**
   * 在分布式锁中执行方法，并指定是否阻塞、锁过期时间和获取等待时间.
   *
   * @param lockId 锁id, 不能为空
   * @param blocked 获取不到锁时是否阻塞等待
   * @param expiredSeconds 锁的过期时间，单位为秒
   * @param acquireWaitSeconds 获取锁的等待时间，单位为秒，不能为负数
   * @param supplier 获取锁后执行的方法
   * @param <T> 方法返回值类型
   * @return 方法执行结果
   */
  public <T> T withLock(
      String lockId,
      boolean blocked,
      long expiredSeconds,
      long acquireWaitSeconds,
      Supplier<T> supplier) {
    try (var ignored = lockFactory.acquireLock(
        lockId,
        blocked,
        TimeUnit.SECONDS.toMillis(expiredSeconds),
        TimeUnit.SECONDS.toMillis(acquireWaitSeconds))) {
      return supplier.get();
    }
  }
}
