package com.tehang.common.utility.time

import com.tehang.common.TestSpecification
import com.tehang.common.utility.DateUtils

class BjDateSpec extends TestSpecification {

  def "test1: BjDate.today() is OK"() {
    when:
    String todayString = DateUtils.nowOfCst().toString(BjDate.DATE_FORMAT_TO_DAY)
    BjDate today = BjDate.today()

    then:
    todayString == today.toString()
  }

  def "test2: new BjDate(String dateString) is OK"() {
    when:
    String todayString = DateUtils.nowOfCst().toString(BjDate.DATE_FORMAT_TO_DAY)
    BjDate today = new BjDate(todayString)

    then:
    todayString == today.toString()
  }

  def "test3: new BjDate(String dateString) with invalid date format get IllegalArgumentException"() {
    when:
    String invalidDate = "20220315"
    BjDate date = new BjDate(invalidDate)

    then:
    thrown(IllegalArgumentException)
  }

  def "test4: BjDate.plusDays() is OK"() {
    when:
    String dayString = DateUtils.nowOfCst().plusDays(1).toString(BjDate.DATE_FORMAT_TO_DAY)
    BjDate day = BjDate.today().plusDays(1)

    then:
    dayString == day.toString()
  }

  def "test5: BjDate.minusDays() is OK"() {
    when:
    String dayString = DateUtils.nowOfCst().minusDays(1).toString(BjDate.DATE_FORMAT_TO_DAY)
    BjDate day = BjDate.today().minusDays(1)

    then:
    dayString == day.toString()
  }
}
