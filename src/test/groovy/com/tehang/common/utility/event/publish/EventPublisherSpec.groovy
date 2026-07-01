package com.tehang.common.utility.event.publish

import com.tehang.common.TestSpecification
import com.tehang.common.utility.event.DefaultEvent
import com.tehang.common.utility.event.mq.MqConfig
import com.tehang.common.utility.event.mq.MqProducer

class EventPublisherSpec extends TestSpecification {

  MqProducer mqProducer = Mock(MqProducer)
  MqConfig mqConfig = Mock(MqConfig)
  EventPublisher eventPublisher = new EventPublisher(mqProducer, mqConfig)

  def setup() {
    mqConfig.getGroupId() >> 'GID-test'
    mqConfig.getTopic() >> 'topic-test'
    mqConfig.getEventTagPrefix() >> 'dev1-'
  }

  def "publish发送MQ时使用eventType和eventKey派生MessageKey"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'

    when:
    eventPublisher.publish(event)

    then:
    1 * mqProducer.sendToQueue('topic-test', 'dev1-FlightBookingCompleted',
        'FlightBookingCompleted_order-1', _ as String, null)
    event.key == 'order-1'
  }
}
