package com.tehang.common.utility.event.consume

import com.tehang.common.TestSpecification
import com.tehang.common.utility.event.DefaultEvent
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class DomainEventConsumeRecordJdbcRepositorySpec extends TestSpecification {

  NamedParameterJdbcTemplate jdbcTemplate = Mock(NamedParameterJdbcTemplate)
  DomainEventConsumeRecordJdbcRepository repository = new DomainEventConsumeRecordJdbcRepository(jdbcTemplate)

  def "insertProcessing插入成功时返回true"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'

    when:
    boolean result = repository.insertProcessing(event, 'tmc-services.flight-booking-completed')

    then:
    1 * jdbcTemplate.update(_ as String, { Map params ->
      params.event_key == 'order-1' &&
          params.event_type == 'FlightBookingCompleted' &&
          params.subscriber_id == 'tmc-services.flight-booking-completed' &&
          params.status == DomainEventConsumeStatus.Processing.toString() &&
          params.consume_time == null &&
          params.error_message == null
    }) >> 1
    result
  }

  def "insertProcessing遇到唯一键冲突时返回false"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'

    when:
    boolean result = repository.insertProcessing(event, 'tmc-services.flight-booking-completed')

    then:
    1 * jdbcTemplate.update(_ as String, _ as Map) >> { throw new DuplicateKeyException('duplicated') }
    !result
  }

  def "updateSuccess更新成功状态和消费时间"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'

    when:
    repository.updateSuccess(event, 'tmc-services.flight-booking-completed')

    then:
    1 * jdbcTemplate.update({ String sql ->
      sql.contains('update domain_event_consume_record') &&
          sql.contains('where event_key = :event_key') &&
          sql.contains('and event_type = :event_type') &&
          sql.contains('and subscriber_id = :subscriber_id')
    }, { Map params ->
      params.event_key == 'order-1' &&
          params.event_type == 'FlightBookingCompleted' &&
          params.subscriber_id == 'tmc-services.flight-booking-completed' &&
          params.status == DomainEventConsumeStatus.Success.toString() &&
          params.consume_time != null &&
          params.update_time != null
    }) >> 1
  }
}
