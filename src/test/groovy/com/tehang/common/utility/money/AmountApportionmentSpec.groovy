package com.tehang.common.utility.money

import com.tehang.common.TestSpecification

/** 金额分摊的测试类 */
class AmountApportionmentSpec extends TestSpecification {

  def "test1.0: AmountApportionment.apportion for Money"() {
    when:
    Money totalAmount = new Money(10)
    Money ratio1 = new Money(1)
    Money ratio2 = new Money(1)
    Money ratio3 = new Money(1)

    List<Money> resultsByCent = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3), ApportionPrecision.Cent)
    List<Money> resultsByYuan = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3), ApportionPrecision.Yuan)

    then:
    resultsByCent.size() == 3
    resultsByCent[0] == new Money(3.33)
    resultsByCent[1] == new Money(3.33)
    resultsByCent[2] == new Money(3.34)

    resultsByYuan.size() == 3
    resultsByYuan[0] == new Money(3)
    resultsByYuan[1] == new Money(3)
    resultsByYuan[2] == new Money(4)
  }

  def "test1.1: AmountApportionment.apportion with zero ratios"() {
    when:
    Money totalAmount = new Money(900)
    Money ratio1 = new Money(0)
    Money ratio2 = new Money(0)
    Money ratio3 = new Money(0)

    List<Money> resultsByCent = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3), ApportionPrecision.Cent)
    List<Money> resultsByYuan = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3), ApportionPrecision.Yuan)

    then:
    resultsByCent.size() == 3
    resultsByCent[0] == new Money(300)
    resultsByCent[1] == new Money(300)
    resultsByCent[2] == new Money(300)

    resultsByYuan.size() == 3
    resultsByYuan[0] == new Money(300)
    resultsByYuan[1] == new Money(300)
    resultsByYuan[2] == new Money(300)
  }

  def "test1.2: AmountApportionment.apportion with different ratios"() {
    when:
    Money totalAmount = new Money(900.01)
    Money ratio1 = new Money(0)
    Money ratio2 = new Money(2)
    Money ratio3 = new Money(3)
    Money ratio4 = new Money(4)
    Money ratio5 = new Money(0)

    List<Money> resultsByCent = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3, ratio4, ratio5), ApportionPrecision.Cent)
    List<Money> resultsByYuan = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3, ratio4, ratio5), ApportionPrecision.Yuan)

    then:
    resultsByCent.size() == 5
    resultsByCent[0] == new Money(0)
    resultsByCent[1] == new Money(200)
    resultsByCent[2] == new Money(300)
    resultsByCent[3] == new Money(400.01)  // 多出的一分钱，分摊到最后一项不为0金额上
    resultsByCent[4] == new Money(0)

    resultsByYuan.size() == 5
    resultsByYuan[0] == new Money(0)
    resultsByYuan[1] == new Money(200)
    resultsByYuan[2] == new Money(300)
    resultsByYuan[3] == new Money(400.01)  // 多出的一分钱，分摊到最后一项不为0金额上
    resultsByYuan[4] == new Money(0)
  }

  def "test1.3: AmountApportionment.apportion with negative"() {
    when:
    Money totalAmount = new Money(-900.01)
    Money ratio1 = new Money(0)
    Money ratio2 = new Money(-2)
    Money ratio3 = new Money(-3)
    Money ratio4 = new Money(-4)
    Money ratio5 = new Money(0)

    List<Money> resultsByCent = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3, ratio4, ratio5), ApportionPrecision.Cent)
    List<Money> resultsByYuan = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3, ratio4, ratio5), ApportionPrecision.Yuan)

    then:
    resultsByCent.size() == 5
    resultsByCent[0] == new Money(0)
    resultsByCent[1] == new Money(-200)
    resultsByCent[2] == new Money(-300)
    resultsByCent[3] == new Money(-400.01)  // 多出的一分钱，分摊到最后一项不为0金额上
    resultsByCent[4] == new Money(0)

    resultsByYuan.size() == 5
    resultsByYuan[0] == new Money(0)
    resultsByYuan[1] == new Money(-200)
    resultsByYuan[2] == new Money(-300)
    resultsByYuan[3] == new Money(-400.01)  // 多出的一分钱，分摊到最后一项不为0金额上
    resultsByYuan[4] == new Money(0)
  }

  def "test2.0: AmountApportionment.apportion for BigDecimal"() {
    when:
    BigDecimal totalAmount = new BigDecimal(10)
    BigDecimal ratio1 = new BigDecimal(1)
    BigDecimal ratio2 = new BigDecimal(1)
    BigDecimal ratio3 = new BigDecimal(1)

    List<BigDecimal> resultsByCent = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3), ApportionPrecision.Cent)
    List<BigDecimal> resultsByYuan = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3), ApportionPrecision.Yuan)

    then:
    resultsByCent.size() == 3
    resultsByCent[0] == new BigDecimal("3.33")
    resultsByCent[1] == new BigDecimal("3.33")
    resultsByCent[2] == new BigDecimal("3.34")

    resultsByYuan.size() == 3
    resultsByYuan[0] == new BigDecimal("3")
    resultsByYuan[1] == new BigDecimal("3")
    resultsByYuan[2] == new BigDecimal("4")
  }

  def "test2.1: AmountApportionment.apportion for BigDecimal with negative"() {
    when:
    BigDecimal totalAmount = new BigDecimal(-10)
    BigDecimal ratio1 = new BigDecimal(-1)
    BigDecimal ratio2 = new BigDecimal(-1)
    BigDecimal ratio3 = new BigDecimal(-1)

    List<BigDecimal> resultsByCent = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3), ApportionPrecision.Cent)
    List<BigDecimal> resultsByYuan = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3), ApportionPrecision.Yuan)

    then:
    resultsByCent.size() == 3
    resultsByCent[0] == new BigDecimal("-3.33")
    resultsByCent[1] == new BigDecimal("-3.33")
    resultsByCent[2] == new BigDecimal("-3.34")

    resultsByYuan.size() == 3
    resultsByYuan[0] == new BigDecimal("-3")
    resultsByYuan[1] == new BigDecimal("-3")
    resultsByYuan[2] == new BigDecimal("-4")
  }

  def "test3.0: AmountApportionment.apportion for Long"() {
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

  def "test3.1: AmountApportionment.apportion for Long with negative"() {
    when:
    long totalAmount = -1000
    long ratio1 = -1
    long ratio2 = -1
    long ratio3 = -1

    List<Long> result = AmountApportionment.apportion(totalAmount, List.of(ratio1, ratio2, ratio3))

    then:
    result.size() == 3
    result[0] == -333
    result[1] == -333
    result[2] == -334
  }
}
