package com.tehang.common.utility.time

import com.tehang.common.TestSpecification

class BjTimeToMSSpec extends TestSpecification {

  def "test1: BjTimeToMS.now() is OK"() {
    when:
    BjTimeToMS now = BjTimeToMS.now()
    String nowString = now.getInnerTime().toString(BjDate.DATE_FORMAT_TO_MS)

    then:
    now.toString() == nowString
  }

  def "test2: new BjTimeToMS(String dateString) is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTimeToMS time = new BjTimeToMS(timeString)

    then:
    timeString == time.toString()
  }

  def "test3: new BjTimeToMS(String dateString) with invalid time format get IllegalArgumentException"() {
    when:
    String invalidDate = "2022-03-15 10:30:00"
    BjTimeToMS date = new BjTimeToMS(invalidDate)

    then:
    thrown(IllegalArgumentException)
  }

  def "test4: BjTimeToMS.plusDays() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTimeToMS time = new BjTimeToMS(timeString);

    then:
    time.plusDays(1).toString() == "2022-03-16 10:30:20.555"
  }

  def "test5: BjTimeToMS.minusDays() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTimeToMS time = new BjTimeToMS(timeString);

    then:
    time.minusDays(1).toString() == "2022-03-14 10:30:20.555"
  }

  def "test6: BjTimeToMS.plusSeconds() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTimeToMS time = new BjTimeToMS(timeString);

    then:
    time.plusSeconds(1).toString() == "2022-03-15 10:30:21.555"
  }

  def "test7: BjTimeToMS.minusSeconds() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20.555"
    BjTimeToMS time = new BjTimeToMS(timeString);

    then:
    time.minusSeconds(1).toString() == "2022-03-15 10:30:19.555"
  }

  def "test8: BjTimeToMS.isAfter, isBefore, isEqual, compareTo, equals test"() {
    when:
    BjTimeToMS date1 = new BjTimeToMS("2022-04-08 10:30:00.000")
    BjTimeToMS date2 = new BjTimeToMS("2022-04-08 10:31:00.000")
    BjTimeToMS date3 = new BjTimeToMS("2022-04-08 10:31:00.000")

    then:
    date1.isBefore(date2)
    date2.isAfter(date1)
    date2.isEqual(date3)
    !date1.isEqual(date2)

    date1.compareTo(date2) < 0
    date2.compareTo(date1) > 0
    date2.compareTo(date3) == 0
  }

  def "test9: BjTimeToMS.isAfterNow, isBeforeNow test"() {
    when:
    BjTimeToMS date1 = new BjTimeToMS("2022-04-08 10:35:00.000")
    BjTimeToMS date2 = new BjTimeToMS("2080-01-01 00:00:00.000")

    then:
    date1.isBeforeNow()
    date2.isAfterNow()
  }
}
