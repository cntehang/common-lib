package com.tehang.common.utility.time

import com.tehang.common.TestSpecification

class BjDateTimeFormatSpec extends TestSpecification {

  def "test1: BjDate.format is OK"() {
    expect:
    BjDate.isValid("0000-01-12")
    BjDate.isValid("0001-01-12")
    BjDate.isValid("0457-01-12")
    BjDate.isValid("1000-01-12")
    BjDate.isValid("1640-01-12")
    BjDate.isValid("2240-01-12")
    BjDate.isValid("9999-01-12")

    !BjDate.isValid("476-01-12")
    !BjDate.isValid("2023-01-12:08")
    !BjDate.isValid("-1001-01-12:08")
  }

  def "test2: BjTimeToMinute.format is OK"() {
    expect:
    BjTimeToMinute.isValid("0000-01-12 23:30")
    BjTimeToMinute.isValid("1640-01-12 23:30")
    BjTimeToMinute.isValid("9999-01-12 23:30")
  }

  def "test3: BjTimeToSecond.format is OK"() {
    expect:
    BjTimeToSecond.isValid("0000-01-12 23:30:59")
    BjTimeToSecond.isValid("1640-01-12 23:30:59")
    BjTimeToSecond.isValid("9999-01-12 23:30:59")
  }

  def "test4: BjTime.format is OK"() {
    expect:
    BjTime.isValid("0000-01-12 23:30:59.555")
    BjTime.isValid("1640-01-12 23:30:59.555")
    BjTime.isValid("9999-01-12 23:30:59.555")
  }
}
