package com.tehang.common.utility.money

import com.tehang.common.TestSpecification
import com.tehang.common.utility.JsonUtils

class PercentSpec extends TestSpecification {

  def "test1: Percent.ZERO"() {
    expect:
    Percent.ZERO.toString() == "0%"
    Percent.ZERO.toNoneDecimalString() == "0%"
    Percent.ZERO.toOneDecimalString() == "0.0%"
    Percent.ZERO.toTwoDecimalString() == "0.00%"
  }

  def "test2: new Percent(String percentString)"() {
    when:
    Percent percent1 = new Percent("1.5")
    Percent percent2 = new Percent("1.015")

    then:
    percent1.toString() == "1.5%"
    percent1.toTwoDecimalString() == "1.50%"

    percent2.toString() == "1.02%"
    percent2.toTwoDecimalString() == "1.02%"
  }

  def "test3: Percent compares"() {
    when:
    Percent percent10 = new Percent("10")
    Percent percent20 = new Percent("20")

    then:
    percent10 != percent20
    percent10 < percent20
    percent10.add(percent10) == percent20
  }

  def "test4: getPercentValue"() {
    expect:
    new Percent(20).getPercentValue(new Money(5)) == new Money(1)
    new Percent(30).getPercentValue(new Money(5)) == new Money(1.5)
  }

  def "test5: getPercent by money"() {
    expect:
    Percent.getPercent(new Money(1), new Money(5)) == new Percent(20)
    Percent.getPercent(new Money(1), new Money(8)) == new Percent(12.5)
    Percent.getPercent(new Money(1), new Money(3)) == new Percent(33.33)
  }

  def "test7: Percent JsonSerializer"() {
    expect:
    JsonUtils.toJson(new Percent('151.465')) == '151.47'
    JsonUtils.toJson(new Percent('-99.50')) == '-99.5'
    JsonUtils.toJson(new Percent('0.00')) == '0'
    JsonUtils.toJson(new Percent('0')) == '0'

    JsonUtils.toClass('151.47', Percent.class) == new Percent('151.47')
    JsonUtils.toClass('-99.5', Percent.class) == new Percent('-99.5')
    JsonUtils.toClass('0.00', Percent.class) == new Percent('0')
    JsonUtils.toClass('0', Percent.class) == new Percent('0')
  }
}
