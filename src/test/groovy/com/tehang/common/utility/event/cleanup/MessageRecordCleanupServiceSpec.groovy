package com.tehang.common.utility.event.cleanup

import com.tehang.common.TestSpecification
import com.tehang.common.utility.time.BjTime
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus

class MessageRecordCleanupServiceSpec extends TestSpecification {

  MessageRecordCleanupProperties properties = new MessageRecordCleanupProperties()
  MessageRecordCleanupJdbcRepository repository = Mock(MessageRecordCleanupJdbcRepository)
  TransactionStatus transactionStatus = Stub(TransactionStatus)
  PlatformTransactionManager transactionManager = Stub(PlatformTransactionManager) {
    getTransaction(_ as TransactionDefinition) >> transactionStatus
  }
  MessageRecordCleanupService service = new MessageRecordCleanupService(properties, repository, transactionManager)

  def setup() {
    properties.batchSize = 2
    properties.maxBatchesPerRun = 3
    properties.eventRecordEnabled = false
    properties.consumeRecordEnabled = false
    properties.commandRecordEnabled = false
  }

  def "事件记录按批次清理并在不满一批时停止"() {
    given:
    properties.eventRecordEnabled = true

    when:
    MessageRecordCleanupResult result = service.cleanup()

    then:
    1 * repository.countExpiredWaitSendRecords(_ as BjTime) >> 0L
    2 * repository.deleteEventRecordsBefore(_ as BjTime, 2) >>> [2, 1]
    result.eventRecordCount == 3
    result.eventRecordBatchCount == 2
    result.consumeRecordCount == 0
    result.commandRecordCount == 0
  }

  def "清理达到最大批次后停止"() {
    given:
    properties.consumeRecordEnabled = true

    when:
    MessageRecordCleanupResult result = service.cleanup()

    then:
    1 * repository.countExpiredProcessingRecords(_ as BjTime) >> 0L
    3 * repository.deleteConsumeRecordsBefore(_ as BjTime, 2) >> 2
    result.consumeRecordCount == 6
    result.consumeRecordBatchCount == 3
  }

  def "命令无论成功失败都按eventKey整组清理"() {
    given:
    properties.commandRecordEnabled = true

    when:
    MessageRecordCleanupResult result = service.cleanup()

    then:
    2 * repository.deleteCommandHistoryBefore(_ as BjTime, 2) >>> [2, 0]
    1 * repository.findExpiredCommandEventKeysBefore(_ as BjTime, 2) >> ['event-1', 'event-2']
    1 * repository.deleteCommandHistoryByEventKeys(['event-1', 'event-2']) >> 1
    1 * repository.deleteCommandRecordsByEventKeys(['event-1', 'event-2']) >> 4
    1 * repository.findExpiredCommandEventKeysBefore(_ as BjTime, 2) >> []
    result.commandHistoryCount == 3
    result.commandHistoryBatchCount == 3
    result.commandRecordCount == 4
    result.commandRecordBatchCount == 1
  }

  def "关闭的数据表不执行查询或删除"() {
    when:
    MessageRecordCleanupResult result = service.cleanup()

    then:
    0 * repository._
    result.eventRecordCount == 0
    result.consumeRecordCount == 0
    result.commandRecordCount == 0
    result.commandHistoryCount == 0
    result.eventRecordBatchCount == 0
    result.consumeRecordBatchCount == 0
    result.commandRecordBatchCount == 0
    result.commandHistoryBatchCount == 0
  }

  def "批次配置无效时拒绝执行"() {
    given:
    properties.batchSize = 0

    when:
    service.cleanup()

    then:
    thrown(IllegalArgumentException)
    0 * repository._
  }
}
