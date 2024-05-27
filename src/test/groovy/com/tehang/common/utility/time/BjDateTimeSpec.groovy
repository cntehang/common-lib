package com.tehang.common.utility.time

import com.tehang.common.TestSpecification

class BjDateTimeSpec extends TestSpecification {

  def "test1: BjDateTime.getTotalInterval() is OK"() {
    when:
    BjTimeToMinute start = BjTimeToMinute.parse("2023-01-10 10:00");
    BjTimeToMinute end = BjTimeToMinute.parse("2023-01-10 11:40");

    double intervalOfDay = BjDateTime.getTotalInterval(start, end, TimeIntervalType.Day);
    double intervalOfHour = BjDateTime.getTotalInterval(start, end, TimeIntervalType.Hour);
    double intervalOfMinute = BjDateTime.getTotalInterval(start, end, TimeIntervalType.Minute);

    then:
    (int)Math.floor(intervalOfDay) == 0
    (int)Math.floor(intervalOfHour) == 1
    (int)Math.floor(intervalOfMinute) == 100
  }

  def "test2: BjDateTime convert test"() {
    expect:
    new BjTime("2024-05-25 17:59:30.500").toDate() == new BjDate("2024-05-25")
    new BjTime("2024-05-25 17:59:30.500").toTimeInMinute() == new BjTimeToMinute("2024-05-25 17:59")
    new BjTime("2024-05-25 17:59:30.500").toTimeInSecond() == new BjTimeToSecond("2024-05-25 17:59:30")
    new BjTime("2024-05-25 17:59:30.500").toTime() == new BjTime("2024-05-25 17:59:30.500")

    new BjDate("2024-05-25").toDate() == new BjDate("2024-05-25")
    new BjDate("2024-05-25").toTimeInMinute() == new BjTimeToMinute("2024-05-25 00:00")
    new BjDate("2024-05-25").toTimeInSecond() == new BjTimeToSecond("2024-05-25 00:00:00")
    new BjDate("2024-05-25").toTime() == new BjTime("2024-05-25 00:00:00.000")
  }

  def "test3: BjDateTime.toUtcTimeString() test"() {
    expect:
    new BjDate("2024-05-25").toUtcTimeString() == "2024-05-24T16:00:00.000Z"
    new BjTimeToMinute("2024-05-25 18:59").toUtcTimeString() == "2024-05-25T10:59:00.000Z"
    new BjTime("2024-05-25 18:59:30.500").toUtcTimeString() == "2024-05-25T10:59:30.500Z"
  }

  def "test4: BjTime.parseInUtc() test"() {
    expect:
    BjTime.parseInUtc("2024-05-25T10:59:30.500Z") == new BjTime("2024-05-25 18:59:30.500")
    BjTime.parseInUtc("2024-05-24T16:00:00.000Z") == new BjTime("2024-05-25 00:00:00.000")
  }
}
