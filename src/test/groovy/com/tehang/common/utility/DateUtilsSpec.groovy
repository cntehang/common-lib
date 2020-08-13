/*
 * Copyright (c) 2020 MAMCHARGE 深圳智链物联科技有限公司. <http://www.mamcharge.com>
 *
 */

package com.tehang.common.utility

import com.tehang.common.TestSpecification
import org.apache.commons.lang3.StringUtils
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
    StringUtils.equals(timeString, formattedTimeString)
  }

  def "测试2： String -> Instant"() {
    when:
    Instant instantFromString = DateUtils.instantFromString(timeString)

    then:
    noExceptionThrown()
    instant == instantFromString
  }
}
