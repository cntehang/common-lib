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
}
