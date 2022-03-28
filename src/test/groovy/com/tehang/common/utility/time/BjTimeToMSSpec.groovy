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
}
