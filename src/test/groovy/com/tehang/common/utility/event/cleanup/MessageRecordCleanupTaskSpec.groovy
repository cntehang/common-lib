package com.tehang.common.utility.event.cleanup

import com.tehang.common.TestSpecification
import com.tehang.common.utility.lock.DistributedLockHelper
import com.tehang.common.utility.lock.LockNotAcquiredException

import java.util.function.Supplier

class MessageRecordCleanupTaskSpec extends TestSpecification {

  MessageRecordCleanupService cleanupService = Mock(MessageRecordCleanupService)
  DistributedLockHelper lockHelper = Mock(DistributedLockHelper)
  MessageRecordCleanupTask task = new MessageRecordCleanupTask(cleanupService, lockHelper)

  def "获取分布式锁后执行清理"() {
    given:
    def result = new MessageRecordCleanupResult()

    when:
    task.cleanupMessageRecords()

    then:
    1 * lockHelper.withLock('MessageRecordCleanupTask', false, 1800, _ as Supplier) >> {
      String lockId, boolean blocked, long expiredSeconds, Supplier supplier -> supplier.get()
    }
    1 * cleanupService.cleanup() >> result
  }

  def "未获取分布式锁时跳过清理"() {
    when:
    task.cleanupMessageRecords()

    then:
    1 * lockHelper.withLock('MessageRecordCleanupTask', false, 1800, _ as Supplier) >> {
      throw new LockNotAcquiredException('locked')
    }
    0 * cleanupService.cleanup()
    noExceptionThrown()
  }
}
