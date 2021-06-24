package com.tehang.common.utility.lock;

import com.tehang.common.utility.redis.TmcRedisOperator;
import lombok.extern.slf4j.Slf4j;

/**
 *  分布式锁的辅助类：非阻塞，不可重入
 */
@Slf4j
public class DistributedLock implements AutoCloseable {

  private final String lockKey;
  private final TmcRedisOperator redisOperator;

  public DistributedLock(String lockKey, TmcRedisOperator redisOperator) {
    this.lockKey = lockKey;
    this.redisOperator = redisOperator;
  }

  @Override
  public void close() {
    releaseLock();
  }

  /**
   * 释放分布式锁
   */
  private void releaseLock() {
    Boolean success = redisOperator.delete(lockKey);
    log.debug("releaseLock, lockKey: {}, result: {}", lockKey, success);
  }
}
