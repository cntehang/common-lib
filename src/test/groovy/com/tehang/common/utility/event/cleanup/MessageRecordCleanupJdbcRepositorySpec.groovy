package com.tehang.common.utility.event.cleanup

import com.tehang.common.TestSpecification
import com.tehang.common.utility.time.BjTime
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class MessageRecordCleanupJdbcRepositorySpec extends TestSpecification {

  NamedParameterJdbcTemplate jdbcTemplate = Mock(NamedParameterJdbcTemplate)
  MessageRecordCleanupJdbcRepository repository = new MessageRecordCleanupJdbcRepository(jdbcTemplate)
  BjTime cutoffTime = BjTime.parse('2026-04-13 03:30:00.000')

  def "事件记录只清理过期终态数据"() {
    when:
    int count = repository.deleteEventRecordsBefore(cutoffTime, 1000)

    then:
    1 * jdbcTemplate.update({ String sql ->
      sql.contains("status in ('SendSuccess', 'SendFailed')") &&
          sql.contains('create_time < :cutoff_time') &&
          sql.contains('limit :batch_size')
    }, { Map params ->
      params.cutoff_time == cutoffTime.toString() && params.batch_size == 1000
    }) >> 10
    count == 10
  }

  def "消费记录只清理过期成功数据"() {
    when:
    int count = repository.deleteConsumeRecordsBefore(cutoffTime, 500)

    then:
    1 * jdbcTemplate.update({ String sql ->
      sql.contains("status = 'Success'") && sql.contains('create_time < :cutoff_time')
    }, { Map params -> params.batch_size == 500 }) >> 8
    count == 8
  }

  def "过期命令查询不判断执行状态"() {
    when:
    List<String> eventKeys = repository.findExpiredCommandEventKeysBefore(cutoffTime, 100)

    then:
    1 * jdbcTemplate.queryForList({ String sql ->
      sql.contains('expired.create_time < :cutoff_time') &&
          sql.contains('retained.create_time >= :cutoff_time') &&
          !sql.toLowerCase().contains('success')
    }, { Map params -> params.batch_size == 100 }, String.class) >> ['event-1', 'event-2']
    eventKeys == ['event-1', 'event-2']
  }

  def "命令记录按eventKey先删历史再删主记录"() {
    given:
    def eventKeys = ['event-1', 'event-2']

    when:
    int historyCount = repository.deleteCommandHistoryByEventKeys(eventKeys)
    int commandCount = repository.deleteCommandRecordsByEventKeys(eventKeys)

    then:
    1 * jdbcTemplate.update({ String sql ->
      sql.contains('delete his from command_record_his') && sql.contains('inner join command_record')
    }, { Map params -> params.event_keys == eventKeys }) >> 3
    1 * jdbcTemplate.update({ String sql ->
      sql.contains('delete from command_record where event_key in')
    }, { Map params -> params.event_keys == eventKeys }) >> 4
    historyCount == 3
    commandCount == 4
  }

  def "超期待发送事件统计使用截止时间"() {
    when:
    long count = repository.countExpiredWaitSendRecords(cutoffTime)

    then:
    1 * jdbcTemplate.queryForObject({ String sql ->
      sql.contains("status = 'WaitSend'")
    }, { Map params -> params.cutoff_time == cutoffTime.toString() }, Long.class) >> 2L
    count == 2L
  }
}
