package com.tehang.common.utility.money

import com.tehang.common.TestSpecification

/** 金额分摊的测试类 */
class AmountApportionmentSpec extends TestSpecification {

  def "test1: AmountApportionment.apportion for Money"() {
    when:
    Money totalAmount = new Money(10)
    Money ratio1 = new Money(1)
    Money ratio2 = new Money(1)
    Money ratio3 = new Money(1)

    List<Money> result = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3))

    then:
    result.size() == 3
    result[0] == new Money(3.33)
    result[1] == new Money(3.33)
    result[2] == new Money(3.34)
  }

  def "test1: AmountApportionment.apportion with zero ratios"() {
    when:
    Money totalAmount = new Money(900)
    Money ratio1 = new Money(0)
    Money ratio2 = new Money(0)
    Money ratio3 = new Money(0)

    List<Money> result = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3))

    then:
    result.size() == 3
    result[0] == new Money(300)
    result[1] == new Money(300)
    result[2] == new Money(300)
  }

  def "test1.1: AmountApportionment.apportion with different ratios"() {
    when:
    Money totalAmount = new Money(900.01)
    Money ratio1 = new Money(0)
    Money ratio2 = new Money(2)
    Money ratio3 = new Money(3)
    Money ratio4 = new Money(4)
    Money ratio5 = new Money(0)

    List<Money> result = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3, ratio4, ratio5))

    then:
    result.size() == 5
    result[0] == new Money(0)
    result[1] == new Money(200)
    result[2] == new Money(300)
    result[3] == new Money(400.01)  // 多出的一分钱，分摊到最后一项不为0金额上
    result[4] == new Money(0)
  }

  def "test2: AmountApportionment.apportion for BigDecimal"() {
    when:
    BigDecimal totalAmount = new BigDecimal("10")
    BigDecimal ratio1 = new BigDecimal("1")
    BigDecimal ratio2 = new BigDecimal("1")
    BigDecimal ratio3 = new BigDecimal("1")

    List<BigDecimal> result = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3))

    then:
    result.size() == 3
    result[0] == new BigDecimal("3.33")
    result[1] == new BigDecimal("3.33")
    result[2] == new BigDecimal("3.34")
  }

  def "test3: AmountApportionment.apportion for Long"() {
    when:
    long totalAmount = 1000
    long ratio1 = 1
    long ratio2 = 1
    long ratio3 = 1

    List<Long> result = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3))

    then:
    result.size() == 3
    result[0] == 333
    result[1] == 333
    result[2] == 334
  }
}
