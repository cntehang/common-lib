package com.tehang.common.utility

import com.tehang.common.TestSpecification
import spock.lang.Shared

import java.time.Instant

class DateUtilsSpec extends TestSpecification {

  @Shared
  Instant instant = Instant.ofEpochMilli(1597298947555)

  @Shared
  String timeString = "2020-08-13T06:09:07.555Z"

  def "测试1： Instant -> String"() {
    when:
    String formattedTimeString = DateUtils.instantToString(instant)

    then:
    noExceptionThrown()
    timeString == formattedTimeString
  }

  def "测试2： String -> Instant"() {
    when:
    Instant instantFromString = DateUtils.instantFromString(timeString)

    then:
    noExceptionThrown()
    instant == instantFromString
  }
}
