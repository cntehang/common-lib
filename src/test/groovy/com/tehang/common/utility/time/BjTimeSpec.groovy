package com.tehang.common.utility.time

import com.tehang.common.TestSpecification

class BjTimeSpec extends TestSpecification {

  def "test1: BjTime.now() is OK"() {
    when:
    BjTime now = BjTime.now()
    String nowString = now.getInnerTime().toString(BjDate.DATE_FORMAT_TO_MS)

    then:
    now.toString() == nowString
  }

  def "test2: new BjTime(String dateString) is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTime time = new BjTime(timeString)

    then:
    timeString == time.toString()
  }

  def "test3: new BjTime(String dateString) with invalid time format get IllegalArgumentException"() {
    when:
    String invalidDate = "2022-03-15 10:30:00"
    BjTime date = new BjTime(invalidDate)

    then:
    thrown(IllegalArgumentException)
  }

  def "test4: BjTime.plusDays() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTime time = new BjTime(timeString);

    then:
    time.plusDays(1).toString() == "2022-03-16 10:30:20.555"
  }

  def "test5: BjTime.minusDays() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTime time = new BjTime(timeString);

    then:
    time.minusDays(1).toString() == "2022-03-14 10:30:20.555"
  }

  def "test6: BjTime.plusSeconds() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTime time = new BjTime(timeString);

    then:
    time.plusSeconds(1).toString() == "2022-03-15 10:30:21.555"
  }

  def "test7: BjTime.minusSeconds() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTime time = new BjTime(timeString);

    then:
    time.minusSeconds(1).toString() == "2022-03-15 10:30:19.555"
  }

  def "test8: BjTime.isAfter, isBefore, isEqual, compareTo, equals test"() {
    when:
    BjTime date1 = new BjTime("2022-04-08 10:30:00.000")
    BjTime date2 = new BjTime("2022-04-08 10:31:00.000")
    BjTime date3 = new BjTime("2022-04-08 10:31:00.000")

    then:
    date1.isBefore(date2)
    date2.isAfter(date1)
    date2.isEqual(date3)
    !date1.isEqual(date2)

    date1.compareTo(date2) < 0
    date2.compareTo(date1) > 0
    date2.compareTo(date3) == 0
  }

  def "test9: BjTime.isAfterNow, isBeforeNow test"() {
    when:
    BjTime date1 = new BjTime("2022-04-08 10:35:00.000")
    BjTime date2 = new BjTime("2080-01-01 00:00:00.000")

    then:
    date1.isBeforeNow()
    date2.isAfterNow()
  }
}
