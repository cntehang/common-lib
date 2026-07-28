package com.tehang.common.utility.lock

import com.tehang.common.TestSpecification
import com.tehang.common.utility.redis.CommonRedisOperator

import java.util.concurrent.TimeUnit

class DistributedLockFactorySpec extends TestSpecification {

  CommonRedisOperator redisOperator = Mock(CommonRedisOperator)
  DistributedLockFactory lockFactory = new DistributedLockFactory(redisOperator)

  def "首次获取成功时使用指定的锁过期时间"() {
    when:
    def lock = lockFactory.acquireLock('test-lock', true, 1500, 100)

    then:
    1 * redisOperator.setIfAbsent('LOCK_PREFIXtest-lock', _ as String, 1500, TimeUnit.MILLISECONDS) >> true
    lock != null
  }

  def "阻塞获取在较短的自定义等待时间后超时"() {
    given:
    long startTime = System.nanoTime()

    when:
    lockFactory.acquireLock('test-lock', true, 1500, 100)

    then:
    2 * redisOperator.setIfAbsent('LOCK_PREFIXtest-lock', _ as String, 1500, TimeUnit.MILLISECONDS) >> false
    thrown(LockTimeoutException)
    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) < 1500
  }

  def "非阻塞获取失败时不等待"() {
    when:
    lockFactory.acquireLock('test-lock', false, 1500, 30000)

    then:
    1 * redisOperator.setIfAbsent('LOCK_PREFIXtest-lock', _ as String, 1500, TimeUnit.MILLISECONDS) >> false
    thrown(LockNotAcquiredException)
  }

  def "等待时间为零时首次获取失败后立即超时"() {
    when:
    lockFactory.acquireLock('test-lock', true, 1500, 0)

    then:
    1 * redisOperator.setIfAbsent('LOCK_PREFIXtest-lock', _ as String, 1500, TimeUnit.MILLISECONDS) >> false
    thrown(LockTimeoutException)
  }

  def "等待时间为负数时拒绝获取锁"() {
    when:
    lockFactory.acquireLock('test-lock', true, 1500, -1)

    then:
    thrown(IllegalArgumentException)
    0 * redisOperator._
  }
}
