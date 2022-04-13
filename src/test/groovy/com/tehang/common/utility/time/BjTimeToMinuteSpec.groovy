package com.tehang.common.utility.time

import com.tehang.common.TestSpecification
import com.tehang.common.utility.DateUtils

class BjTimeToMinuteSpec extends TestSpecification {

  def "test1: BjTimeToMinute.now() is OK"() {
    when:
    BjTimeToMinute now = BjTimeToMinute.now()
    String nowString = now.getInnerTime().toString(BjDate.DATE_FORMAT_TO_MINUTE)

    then:
    now.toString() == nowString
  }

  def "test2: new BjTimeToMinute(String dateString) is OK"() {
    when:
    String nowString = DateUtils.nowOfCst().toString(BjDate.DATE_FORMAT_TO_MINUTE)
    BjTimeToMinute now = new BjTimeToMinute(nowString)

    then:
    nowString == now.toString()
  }

  def "test3: new BjTimeToMinute(String dateString) with invalid time format get IllegalArgumentException"() {
    when:
    String invalidDate = "2022-03-15 10:30:00"
    BjTimeToMinute date = new BjTimeToMinute(invalidDate)

    then:
    thrown(IllegalArgumentException)
  }

  def "test4: BjTimeToMinute.plusDays() is OK"() {
    when:
    String timeString = "2022-03-15 10:30"
    BjTimeToMinute time = new BjTimeToMinute(timeString);

    then:
    time.plusDays(1).toString() == "2022-03-16 10:30"
  }

  def "test5: BjTimeToMinute.minusDays() is OK"() {
    when:
    String timeString = "2022-03-15 10:30"
    BjTimeToMinute time = new BjTimeToMinute(timeString);

    then:
    time.minusDays(1).toString() == "2022-03-14 10:30"
  }

  def "test6: BjTimeToMinute.plusMinutes() is OK"() {
    when:
    String timeString = "2022-03-15 10:30"
    BjTimeToMinute time = new BjTimeToMinute(timeString);

    then:
    time.plusMinutes(1).toString() == "2022-03-15 10:31"
  }

  def "test7: BjTimeToMinute.minusMinutes() is OK"() {
    when:
    String timeString = "2022-03-15 10:30"
    BjTimeToMinute time = new BjTimeToMinute(timeString);

    then:
    time.minusMinutes(1).toString() == "2022-03-15 10:29"
  }

  def "test8: BjTimeToMinute.isAfter, isBefore, isEqual test"() {
    when:
    BjTimeToMinute date1 = new BjTimeToMinute("2022-04-08 10:30")
    BjTimeToMinute date2 = new BjTimeToMinute("2022-04-08 10:31")
    BjTimeToMinute date3 = new BjTimeToMinute("2022-04-08 10:31")

    then:
    date1.isBefore(date2)
    date2.isAfter(date1)
    date2.isEqual(date3)
    !date1.isEqual(date2)
  }

  def "test9: BjTimeToMinute.isAfterNow, isBeforeNow test"() {
    when:
    BjTimeToMinute date1 = new BjTimeToMinute("2022-04-08 10:35")
    BjTimeToMinute date2 = new BjTimeToMinute("2080-01-01 00:00")

    then:
    date1.isBeforeNow()
    date2.isAfterNow()
  }
}
