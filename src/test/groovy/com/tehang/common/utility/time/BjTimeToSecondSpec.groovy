package com.tehang.common.utility.time

import com.tehang.common.TestSpecification

class BjTimeToSecondSpec extends TestSpecification {

  def "test1: BjTimeToSecond.now() is OK"() {
    when:
    BjTimeToSecond now = BjTimeToSecond.now()
    String nowString = now.getInnerTime().toString(BjDate.DATE_FORMAT_TO_SECOND)

    then:
    now.toString() == nowString
  }

  def "test2: new BjTimeToSecond(String dateString) is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20"
    BjTimeToSecond time = new BjTimeToSecond(timeString)

    then:
    timeString == time.toString()
  }

  def "test3: new BjTimeToSecond(String dateString) with invalid time format get IllegalArgumentException"() {
    when:
    String invalidDate = "2022-03-15 10:30"
    BjTimeToSecond date = new BjTimeToSecond(invalidDate)

    then:
    thrown(IllegalArgumentException)
  }

  def "test4: BjTimeToSecond.plusDays() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20"
    BjTimeToSecond time = new BjTimeToSecond(timeString);

    then:
    time.plusDays(1).toString() == "2022-03-16 10:30:20"
  }

  def "test5: BjTimeToSecond.minusDays() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20"
    BjTimeToSecond time = new BjTimeToSecond(timeString);

    then:
    time.minusDays(1).toString() == "2022-03-14 10:30:20"
  }

  def "test6: BjTimeToSecond.plusSeconds() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20"
    BjTimeToSecond time = new BjTimeToSecond(timeString);

    then:
    time.plusSeconds(1).toString() == "2022-03-15 10:30:21"
  }

  def "test7: BjTimeToSecond.minusSeconds() is OK"() {
    when:
    String timeString = "2022-03-15 10:30:20"
    BjTimeToSecond time = new BjTimeToSecond(timeString);

    then:
    time.minusSeconds(1).toString() == "2022-03-15 10:30:19"
  }
}
