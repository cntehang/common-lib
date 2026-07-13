package com.tehang.common.utility.event.publish

import com.tehang.common.TestSpecification
import com.tehang.common.utility.event.DefaultEvent
import com.tehang.common.utility.event.mq.MqConfig
import com.tehang.common.utility.event.mq.MqProducer
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecord
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecordJdbcRepository
import com.tehang.common.utility.lock.DistributedLockHelper

class SendDomainEventRecordsToMqServiceSpec extends TestSpecification {

  MqConfig mqConfig = Mock(MqConfig)
  MqProducer mqProducer = Mock(MqProducer)
  DomainEventRecordJdbcRepository eventRecordJdbcRepository = Mock(DomainEventRecordJdbcRepository)
  DistributedLockHelper lockHelper = Mock(DistributedLockHelper)
  SendDomainEventRecordsToMqService service = new SendDomainEventRecordsToMqService(
      mqConfig, mqProducer, eventRecordJdbcRepository, lockHelper)

  def setup() {
    mqConfig.getTopic() >> 'topic-test'
    mqConfig.getEventTagPrefix() >> 'dev1-'
  }

  def "sendDomainEventRecords发送MQ时使用eventType和eventKey派生MessageKey"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'
    def record = DomainEventRecord.create(event, null, 'GID-test')

    when:
    service.sendDomainEventRecords()

    then:
    1 * eventRecordJdbcRepository.findAllByWaitSend() >> [record]
    1 * lockHelper.withLock(record.id, _ as Runnable) >> { String lockId, Runnable runnable -> runnable.run() }
    1 * eventRecordJdbcRepository.isWaitSend(record.id) >> true
    1 * mqProducer.sendToQueue('topic-test', 'dev1-FlightBookingCompleted',
        'FlightBookingCompleted_order-1', record.body, null)
    1 * eventRecordJdbcRepository.updateOnSendSuccess(record)
  }
}
