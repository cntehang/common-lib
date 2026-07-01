package com.tehang.common.utility.event.consume

import com.tehang.common.TestSpecification
import com.tehang.common.utility.event.DefaultEvent
import com.tehang.common.utility.event.DomainEvent
import com.tehang.common.utility.event.subscriber.DatabaseIdempotentClusteringEventSubscriber
import org.springframework.transaction.annotation.Transactional

class DomainEventConsumeServiceSpec extends TestSpecification {

  DomainEventConsumeRecordJdbcRepository consumeRecordJdbcRepository = Mock(DomainEventConsumeRecordJdbcRepository)
  DomainEventConsumeService consumeService = new DomainEventConsumeService(consumeRecordJdbcRepository)

  def "consume首次消费成功时调用订阅者并更新成功记录"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'
    DatabaseIdempotentClusteringEventSubscriber subscriber = Mock(DatabaseIdempotentClusteringEventSubscriber)

    when:
    boolean result = consumeService.consume(subscriber, event)

    then:
    1 * subscriber.subscriberId() >> 'tmc-services.flight-booking-completed'
    1 * consumeRecordJdbcRepository.insertProcessing(event, 'tmc-services.flight-booking-completed') >> true
    1 * subscriber.handleEvent(event)
    1 * consumeRecordJdbcRepository.updateSuccess(event, 'tmc-services.flight-booking-completed')
    result
  }

  def "consume重复消费时不再调用订阅者"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'
    DatabaseIdempotentClusteringEventSubscriber subscriber = Mock(DatabaseIdempotentClusteringEventSubscriber)

    when:
    boolean result = consumeService.consume(subscriber, event)

    then:
    1 * subscriber.subscriberId() >> 'tmc-services.flight-booking-completed'
    1 * consumeRecordJdbcRepository.insertProcessing(event, 'tmc-services.flight-booking-completed') >> false
    0 * subscriber.handleEvent(_)
    0 * consumeRecordJdbcRepository.updateSuccess(_, _)
    !result
  }

  def "consume订阅者处理异常时不更新成功记录并继续抛出异常"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'
    DatabaseIdempotentClusteringEventSubscriber subscriber = Mock(DatabaseIdempotentClusteringEventSubscriber)

    when:
    consumeService.consume(subscriber, event)

    then:
    1 * subscriber.subscriberId() >> 'tmc-services.flight-booking-completed'
    1 * consumeRecordJdbcRepository.insertProcessing(event, 'tmc-services.flight-booking-completed') >> true
    1 * subscriber.handleEvent(event) >> { throw new RuntimeException('consume failed') }
    0 * consumeRecordJdbcRepository.updateSuccess(_, _)
    thrown(RuntimeException)
  }

  def "consume方法声明事务"() {
    expect:
    DomainEventConsumeService.getDeclaredMethod('consume', DatabaseIdempotentClusteringEventSubscriber, DomainEvent)
        .isAnnotationPresent(Transactional)
  }
}
