package com.tehang.common.utility.money

import com.tehang.common.TestSpecification
import com.tehang.common.utility.JsonUtils

class MoneyRangeSpec extends TestSpecification {

  def "test1: MoneyRange.contains"() {
    when:
    def minPrice = Money.parse("200")
    def maxPrice = Money.parse("300")
    def range1 = MoneyRange.create(minPrice, maxPrice)
    def range2 = MoneyRange.create(maxPrice, minPrice)

    then:
    range1 == range2
    range1 == range1
    range2 == range2

    range1.contains(minPrice)
    range1.contains(maxPrice)
    !range1.contains(Money.parse("150"))
    !range1.contains(Money.parse("301"))
  }

  def "test2: MoneyRange overlapped"() {
    when:
    def range1 = MoneyRange.create(null, Money.parse("200"))
    def range2 = MoneyRange.create(Money.parse("200"), Money.parse("300"))
    def range3 = MoneyRange.create(Money.parse("300"), null)
    def range4 = MoneyRange.create(Money.parse("250"), Money.parse("280"))
    def range5 = MoneyRange.create(Money.parse("100"), Money.parse("500"))
    def range6 = MoneyRange.create(Money.parse("100"), Money.parse("250"))
    def range7 = MoneyRange.create(null, null)

    then:
    range1.overlapped(range1)
    range2.overlapped(range2)

    range1.overlapped(range2)
    range2.overlapped(range3)
    !range1.overlapped(range3)
    !range3.overlapped(range4)
    range4.overlapped(range2)
    range5.overlapped(range2)
    range6.overlapped(range2)
    range5.overlapped(range6)
    range7.overlapped(range1)
    range7.overlapped(range2)
    range7.overlapped(range3)
  }

  def "test3.1: MoneyRange JsonSerialize test"() {
    when:
    def range1 = MoneyRange.create(Money.parse("200"), Money.parse("300"))
    def range2 = MoneyRange.create(Money.parse("200"), null)
    def range3 = MoneyRange.create(null, null)

    then:
    JsonUtils.toJson(range1) == '{"from":200,"to":300}'
    JsonUtils.toJson(range2) == '{"from":200,"to":null}'
    JsonUtils.toJson(range3) == '{"from":null,"to":null}'
  }

  def "test3.2: MoneyRange JsonDeserialize test"() {
    when:
    def range1 = JsonUtils.toClass('{"from":200,"to":300}', MoneyRange.class)
    def range2 = JsonUtils.toClass('{"from":200}', MoneyRange.class)
    def range3 = JsonUtils.toClass('{}', MoneyRange.class)

    then:
    range1 == MoneyRange.create(Money.parse("200"), Money.parse("300"))
    range2 == MoneyRange.create(Money.parse("200"), null)
    range3 == MoneyRange.create(null, null)
  }
}
