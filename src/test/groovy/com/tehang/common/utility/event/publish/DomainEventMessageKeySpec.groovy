package com.tehang.common.utility.event.publish

import com.tehang.common.TestSpecification
import com.tehang.common.utility.event.DefaultEvent

class DomainEventMessageKeySpec extends TestSpecification {

  def "from根据eventType和eventKey生成MQ消息key"() {
    expect:
    DomainEventMessageKey.from('FlightBookingCompleted', 'order-1') == 'FlightBookingCompleted_order-1'
  }

  def "from根据事件对象生成MQ消息key且不修改事件业务key"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'

    when:
    String messageKey = DomainEventMessageKey.from(event)

    then:
    messageKey == 'FlightBookingCompleted_order-1'
    event.key == 'order-1'
  }
}
