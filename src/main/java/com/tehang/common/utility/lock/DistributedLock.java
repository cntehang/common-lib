package com.tehang.common.utility.lock;

import com.tehang.common.utility.redis.CommonRedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

/**
 * 分布式锁的辅助类：非阻塞，不可重入.
 */
@Slf4j
public class DistributedLock implements AutoCloseable {

  private final String lockKey;
  private final String lockValue;
  private final CommonRedisOperator redisOperator;

  public DistributedLock(String lockKey, String lockValue, CommonRedisOperator redisOperator) {
    this.lockKey = lockKey;
    this.lockValue = lockValue;
    this.redisOperator = redisOperator;
  }

  @Override
  public void close() {
    releaseLock();
  }

  /**
   * 释放分布式锁.
   */
  private void releaseLock() {
    // 使用lua脚本释放锁，以保证原子性
    String script = "if redis.call('get', KEYS[1]) == ARGV[1] "
        + "then return redis.call('del', KEYS[1]) "
        + "else return 0 "
        + "end";

    RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
    Long result = redisOperator.execute(redisScript, List.of(this.lockKey), this.lockValue);

    boolean releaseSuccess = result != null && result != 0;
    if (releaseSuccess) {
      log.debug("releaseLock success, lockKey: {}, lockValue: {}, result: {}", lockKey, lockValue, result);
    }
    else {
      // 因redis中的key超时等特殊原因，可能导致锁释放失败
      log.warn("releaseLock failed, lockKey: {}, lockValue: {}, result: {}", lockKey, lockValue, result);
    }
  }
}
