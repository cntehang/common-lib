package com.tehang.common.utility.event.cleanup

import com.tehang.common.TestSpecification

class MessageRecordCleanupPropertiesSpec extends TestSpecification {

  def "清理配置默认关闭并保留三个月"() {
    when:
    def properties = new MessageRecordCleanupProperties()

    then:
    !properties.enabled
    properties.retentionMonths == 3
    properties.batchSize == 1000
    properties.maxBatchesPerRun == 50
    properties.cron == '0 30 3 * * ?'
    properties.eventRecordEnabled
    !properties.consumeRecordEnabled
    !properties.commandRecordEnabled
  }
}
