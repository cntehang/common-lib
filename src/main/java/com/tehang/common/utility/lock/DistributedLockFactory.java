package com.tehang.common.utility.lock;

import com.tehang.common.utility.redis.TmcRedisOperator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 分布式锁的辅助类：不可重入
 */
@Component
@Slf4j
@AllArgsConstructor
public class DistributedLockFactory {

  private static final String LOCK_PREFIX = "LOCK_PREFIX";      //锁前缀
  private static final String LOCK_VALUE = "LOCK_VALUE";        //存储在redis中的值，无实际意义
  private static final long LOCK_EXPIRED_MILLI_SECONDS = 30000;  //锁的过期时间
  private static final long LOCK_TIME_OUT_MILLI_SECONDS = 30000; //获取锁的超时时间，对于阻塞型锁有用

  private final TmcRedisOperator redisOperator;

  /**
   * 获取锁, 非阻塞的，当获取不到锁时，将直接抛出LockNotAcquiredException异常
   * @param lockId 锁id, 不能为空
   * @return
   */
  public DistributedLock acquireLockUnBlocked(String lockId) {
    return acquireLock(lockId, false);
  }

  public DistributedLock acquireLockUnBlocked(String lockId, Duration lockExpireTime) {
    return acquireLock(lockId, false, lockExpireTime.toMillis());
  }

  /**
   * 获取锁, 阻塞式的，当获取不到锁时，将等待并重新尝试获取锁，直到30s超时，并抛出LockTimeoutException异常
   * @param lockId 锁id, 不能为空
   * @return
   */
  public DistributedLock acquireLockWithBlocked(String lockId) {
    return acquireLock(lockId, true);
  }

  /**
   * 获取分布式锁
   */
  private DistributedLock acquireLock(String lockId, boolean blocked) {
    return acquireLock(lockId, blocked, LOCK_EXPIRED_MILLI_SECONDS);
  }

  /**
   * 获取分布式锁
   */
  private DistributedLock acquireLock(String lockId, boolean blocked, long lockExpiredMilliSecond) {
    log.debug("Enter acquireLock: {}", lockId);

    assertLockIdValid(lockId);

    long acquireLockEndTime = System.currentTimeMillis() + LOCK_TIME_OUT_MILLI_SECONDS;
    String lockKey = getRedisKey(lockId);

    if (getLock(lockKey, lockExpiredMilliSecond)) {
      log.debug("Exit acquireLock: {}", lockId);
      return new DistributedLock(lockKey, redisOperator);
    }

    //获取锁失败处理
    if (blocked) {
      //阻塞模式时，尝试重新获取锁
      while (System.currentTimeMillis() < acquireLockEndTime) {
        if (getLock(lockKey, lockExpiredMilliSecond)) {
          log.debug("Exit acquireLock: {}", lockId);
          return new DistributedLock(lockKey, redisOperator);
        }

        //等待2秒，再尝试
        sleep();
      }

      //获取锁超时抛出异常
      String msg = "获取锁超时";
      log.warn(msg);
      throw new LockTimeoutException(msg);

    } else {
      //非阻塞模式时，直接抛出异常
      String msg = "获取锁失败";
      log.warn(msg);
      throw new LockNotAcquiredException(msg);
    }
  }

  /**
   * 获取锁，实质是在redis中设置一个key-value
   */
  private boolean getLock(String lockKey, long lockExpiredMilliSecond) {
    Boolean success = redisOperator.setIfAbsent(lockKey, LOCK_VALUE, lockExpiredMilliSecond, TimeUnit.MILLISECONDS);
    return isTrue(success);
  }

  private void sleep() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException ex) {
      log.warn("sleep调用时发生异常", ex);
      // Restore interrupted state...
      Thread.currentThread().interrupt();
    }
  }

  private static void assertLockIdValid(String lockId) {
    if (isBlank(lockId)) {
      throw new IllegalArgumentException("lockId can not be blank");
    }
  }

  private static String getRedisKey(String lockId) {
    return LOCK_PREFIX + lockId;
  }
}
