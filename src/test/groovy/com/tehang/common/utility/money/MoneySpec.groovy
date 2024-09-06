package com.tehang.common.utility.money

import com.tehang.common.TestSpecification
import com.tehang.common.utility.JsonUtils

class MoneySpec extends TestSpecification {

  def "test1: Money.ZERO is OK"() {
    expect:
    Money.ZERO.toString() == "0"
    Money.ZERO.toTwoDecimalString() == "0.00"
  }

  def "test2: new Money(String amountString) is OK"() {
    when:
    Money money1 = new Money("1.5")
    Money money2 = new Money("1.015")

    then:
    money1.toString() == "1.5"
    money1.toTwoDecimalString() == "1.50"

    money2.toString() == "1.02"
    money2.toTwoDecimalString() == "1.02"
  }

  def "test2-1: new Money(null) throws exception"() {
    when:
    new Money(null)
    then:
    thrown(IllegalArgumentException)
  }

  def "test2-2: new Money('') equals zero"() {
    expect:
    new Money("").wasZero()
  }

  def "test3: new Money(BigDecimal amount) is OK"() {
    when:
    Money money1 = new Money(new BigDecimal("1.5"))
    Money money2 = new Money(new BigDecimal("1.015"))

    then:
    money1.toString() == "1.5"
    money1.toTwoDecimalString() == "1.50"

    money2.toString() == "1.02"
    money2.toTwoDecimalString() == "1.02"
  }

  def "test4: new Money(long value) is OK"() {
    when:
    Money money = new Money(151)

    then:
    money.toString() == "151"
    money.toTwoDecimalString() == "151.00"
    Money.ZERO.toString() == "0"
    Money.ZERO.toTwoDecimalString() == "0.00"
  }

  def "test5: new Money(String amountString) with invalid format get Exception"() {
    when:
    new Money("151..12")

    then:
    thrown(NumberFormatException)
  }

  def "test6.1: Money.sum(Money...) is OK"() {
    when:
    Money money1 = new Money("1")
    Money money2 = new Money("2")
    Money money3 = new Money("3")

    then:
    money1.add(money2) == money3
    Money.sum(money1, money2) == money3
    Money.sum(money1, money2, money3) == new Money("6")
  }

  def "test6.2: Money.multiply(BigDecimal multiplicand) is OK"() {
    when:
    Money money = new Money("2.5")

    then:
    money * 4 == new Money(10)
    money * 0.5 == new Money(1.25)
    money * 0.01 == new Money(0.03)
  }

  def "test7: Money JsonSerializer is OK"() {
    expect:
    JsonUtils.toJson(new Money('151.465')) == '151.47'
    JsonUtils.toJson(new Money('-99.5')) == '-99.5'
    JsonUtils.toJson(new Money('0.00')) == '0'
    JsonUtils.toJson(new Money('0')) == '0'

    JsonUtils.toClass('151.47', Money.class) == new Money('151.47')
    JsonUtils.toClass('-99.5', Money.class) == new Money('-99.5')
    JsonUtils.toClass('0.00', Money.class) == new Money('0')
    JsonUtils.toClass('0', Money.class) == new Money('0')
  }

  def "test8: deserialize Money field support string and number"() {
    expect:
    JsonUtils.toClass('{ "money": 151.00}', DtoWithMoneyField.class).money == new Money('151.00')
    JsonUtils.toClass('{ "money": "151.00"}', DtoWithMoneyField.class).money == new Money('151.00')
    JsonUtils.toClass('{ "money": null}', DtoWithMoneyField.class).money == null
  }
}
