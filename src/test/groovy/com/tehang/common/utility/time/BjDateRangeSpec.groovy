package com.tehang.common.utility.time

import com.tehang.common.TestSpecification
import com.tehang.common.utility.JsonUtils

class BjDateRangeSpec extends TestSpecification {

  def "test1: BjDateRange.contains"() {
    when:
    def start = BjDate.parse("2022-04-15")
    def end = BjDate.parse("2022-04-18")
    def range1 = BjDateRange.create(start, end)

    then:
    range1.contains(start)
    range1.contains(end)
    !range1.contains(BjDate.parse("2022-04-14"))
    !range1.contains(BjDate.parse("2022-04-19"))
  }

  def "test2: BjDateRange start is null"() {
    when:
    def end = BjDate.parse("2022-04-18")
    def range1 = BjDateRange.create(null, end)

    then:
    range1.contains(BjDate.parse("2022-04-15"))
    range1.contains(end)
    !range1.contains(BjDate.parse("2022-04-19"))
  }

  def "test3: BjDateRange end is null"() {
    when:
    def start = BjDate.parse("2022-04-18")
    def range1 = BjDateRange.create(start, null)

    then:
    !range1.contains(BjDate.parse("2022-04-15"))
    range1.contains(start)
    range1.contains(BjDate.parse("2022-04-19"))
  }

  def "test4: BjDateRange start, end all is null"() {
    when:
    def range1 = BjDateRange.create(null, null)

    then:
    range1.contains(BjDate.parse("2022-04-15"))
    range1.contains(BjDate.parse("2022-04-19"))
  }

  def "test5: BjDateRange overlapped"() {
    when:
    def range1 = BjDateRange.create(null, BjDate.parse("2022-04-15"))
    def range2 = BjDateRange.create(BjDate.parse("2022-04-15"), BjDate.parse("2022-04-18"))
    def range3 = BjDateRange.create(BjDate.parse("2022-04-18"), null)
    def range4 = BjDateRange.create(BjDate.parse("2022-04-13"), BjDate.parse("2022-04-14"))
    def range5 = BjDateRange.create(BjDate.parse("2022-04-16"), BjDate.parse("2022-04-20"))
    def range6 = BjDateRange.create(null, null)

    then:
    range1.overlapped(range1)
    range2.overlapped(range2)

    range1.overlapped(range2)
    range2.overlapped(range3)
    !range1.overlapped(range3)
    !range3.overlapped(range4)
    !range4.overlapped(range5)
    range1.overlapped(range6)
    range3.overlapped(range6)
    range5.overlapped(range6)
    range6.overlapped(range6)
  }

  def "test6: JsonSerialize test"() {
    when:
    def range1 = BjDateRange.create(BjDate.parse("2022-04-13"), BjDate.parse("2022-04-14"))
    def range2 = BjDateRange.create(BjDate.parse("2022-04-13"), null)
    def range3 = BjDateRange.create(null, null)

    then:
    JsonUtils.toJson(range1) == '{"from":"2022-04-13","to":"2022-04-14"}'
    JsonUtils.toJson(range2) == '{"from":"2022-04-13"}'
    JsonUtils.toJson(range3) == '{}'
  }

  def "test7: JsonSerialize test"() {
    when:
    def range1 = JsonUtils.toClass('{"from":"2022-04-13","to":"2022-04-14"}', BjDateRange.class)
    def range2 = JsonUtils.toClass('{"from":"2022-04-13"}', BjDateRange.class)
    def range3 = JsonUtils.toClass('{}', BjDateRange.class)

    def date1 = BjDate.parse("2022-04-13")
    def date2 = BjDate.parse("2022-04-14")

    then:
    range1.getFrom().isEqual(date1)
    range1.getTo().isEqual(date2)

    range2.getFrom().isEqual(date1)
    range2.getTo() == null

    range3.getFrom() == null
    range3.getTo() == null
  }
}
