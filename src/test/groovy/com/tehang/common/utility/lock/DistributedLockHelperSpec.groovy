package com.tehang.common.utility.lock

import com.tehang.common.TestSpecification

import java.util.function.Supplier

class DistributedLockHelperSpec extends TestSpecification {

  DistributedLockFactory lockFactory = Mock(DistributedLockFactory)
  DistributedLockHelper lockHelper = new DistributedLockHelper(lockFactory)

  def "Runnable重载换算时间并在锁内执行方法"() {
    given:
    DistributedLock lock = Mock(DistributedLock)
    Runnable runnable = Mock(Runnable)

    when:
    lockHelper.withLock('test-lock', true, 15, 3, runnable)

    then:
    1 * lockFactory.acquireLock('test-lock', true, 15000, 3000) >> lock
    1 * runnable.run()
    1 * lock.close()
  }

  def "Supplier重载换算时间并返回执行结果"() {
    given:
    DistributedLock lock = Mock(DistributedLock)
    Supplier<String> supplier = Mock(Supplier)

    when:
    def result = lockHelper.withLock('test-lock', true, 15, 3, supplier)

    then:
    1 * lockFactory.acquireLock('test-lock', true, 15000, 3000) >> lock
    1 * supplier.get() >> 'result'
    1 * lock.close()
    result == 'result'
  }
}
