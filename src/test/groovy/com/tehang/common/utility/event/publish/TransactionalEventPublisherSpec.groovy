package com.tehang.common.utility.event.publish

import com.tehang.common.TestSpecification
import com.tehang.common.infrastructure.exceptions.SystemErrorException
import com.tehang.common.utility.event.DefaultEvent
import com.tehang.common.utility.event.mq.MqConfig
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecord
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecordJdbcRepository
import com.tehang.common.utility.event.publish.eventrecord.DomainEventSendStatus

class TransactionalEventPublisherSpec extends TestSpecification {

  MqConfig mqConfig = Mock(MqConfig)
  DomainEventRecordJdbcRepository eventRecordJdbcRepository = Mock(DomainEventRecordJdbcRepository)
  EventPublisher eventPublisher = Mock(EventPublisher)
  TransactionalEventPublisher publisher = new TransactionalEventPublisher(mqConfig, eventRecordJdbcRepository, eventPublisher)

  def setup() {
    mqConfig.getGroupId() >> 'GID-test'
  }

  def "publishOnce首次发布成功时写入事件记录并返回true"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    def idempotentKey = 'TMC-FlightBookingCompleted:1001'

    when:
    boolean result = publisher.publishOnce(event, idempotentKey)

    then:
    1 * eventRecordJdbcRepository.addOnce({ DomainEventRecord record ->
      record.eventKey == idempotentKey &&
          record.eventType == 'FlightBookingCompleted' &&
          record.publisher == 'GID-test' &&
          record.status == DomainEventSendStatus.WaitSend
    }) >> true
    result
    event.key == idempotentKey
    0 * eventRecordJdbcRepository.add(_)
  }

  def "publishOnce重复发布时返回false且不抛异常"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    def idempotentKey = 'TMC-FlightBookingCompleted:1001'

    when:
    boolean result = publisher.publishOnce(event, idempotentKey)

    then:
    1 * eventRecordJdbcRepository.addOnce(_ as DomainEventRecord) >> false
    !result
    event.key == idempotentKey
    0 * eventRecordJdbcRepository.add(_)
  }

  def "publishOnce传入空幂等key时抛出异常且不写事件记录"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')

    when:
    publisher.publishOnce(event, ' ')

    then:
    thrown(SystemErrorException)
    0 * eventRecordJdbcRepository._
  }

  def "publishOnce传入超长幂等key时抛出异常且不写事件记录"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    def idempotentKey = 'a' * 101

    when:
    publisher.publishOnce(event, idempotentKey)

    then:
    thrown(SystemErrorException)
    0 * eventRecordJdbcRepository._
  }

  def "普通publish仍使用原有add语义"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')

    when:
    publisher.publish(event)

    then:
    1 * eventRecordJdbcRepository.add(_ as DomainEventRecord)
    0 * eventRecordJdbcRepository.addOnce(_)
  }
}
