package com.tehang.common.utility.lock;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
}
