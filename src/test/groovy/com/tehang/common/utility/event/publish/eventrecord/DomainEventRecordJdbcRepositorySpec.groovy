package com.tehang.common.utility.event.publish.eventrecord

import com.tehang.common.TestSpecification
import com.tehang.common.utility.event.DefaultEvent
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class DomainEventRecordJdbcRepositorySpec extends TestSpecification {

  NamedParameterJdbcTemplate jdbcTemplate = Mock(NamedParameterJdbcTemplate)
  DomainEventRecordJdbcRepository repository = new DomainEventRecordJdbcRepository(jdbcTemplate)

  def "addOnce插入成功时返回true"() {
    given:
    def record = DomainEventRecord.create(new DefaultEvent('FlightBookingCompleted'), null, 'GID-test')

    when:
    boolean result = repository.addOnce(record)

    then:
    1 * jdbcTemplate.update(_ as String, { Map params ->
      params.event_key == record.eventKey &&
          params.event_type == record.eventType &&
          params.publisher == 'GID-test'
    }) >> 1
    result
  }

  def "addOnce遇到唯一键冲突时返回false"() {
    given:
    def record = DomainEventRecord.create(new DefaultEvent('FlightBookingCompleted'), null, 'GID-test')

    when:
    boolean result = repository.addOnce(record)

    then:
    1 * jdbcTemplate.update(_ as String, _ as Map) >> { throw new DuplicateKeyException('duplicated') }
    !result
  }

  def "add遇到唯一键冲突时继续抛出异常"() {
    given:
    def record = DomainEventRecord.create(new DefaultEvent('FlightBookingCompleted'), null, 'GID-test')

    when:
    repository.add(record)

    then:
    1 * jdbcTemplate.update(_ as String, _ as Map) >> { throw new DuplicateKeyException('duplicated') }
    thrown(DuplicateKeyException)
  }
}
