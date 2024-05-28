package com.tehang.common.utility

import com.tehang.common.TestSpecification

import static java.util.stream.Collectors.toList

class CollectionUtilsSpec extends TestSpecification {

  def "test isEmpty"() {
    expect:
    CollectionUtils.isEmpty(null)
    CollectionUtils.isEmpty([])
    !CollectionUtils.isEmpty(["a"])
  }

  def "test isNotEmpty"() {
    expect:
    !CollectionUtils.isNotEmpty(null)
    !CollectionUtils.isNotEmpty([])
    CollectionUtils.isNotEmpty(["a"])
  }

  def "test emptyIfNull"() {
    expect:
    CollectionUtils.emptyIfNull(null).size() == 0
    CollectionUtils.emptyIfNull(["a"]) == ["a"]
  }

  def "test find"() {
    expect:
    CollectionUtils.find(["a", "b"], { item -> (item == "b") }) == "b"
    CollectionUtils.find(null, { item -> (item == "b") }) == null
  }

  def "test findAll"() {
    expect:
    CollectionUtils.findAll(["a", "b"], { item -> (item == "b") }) == ["b"]
    CollectionUtils.findAll(null, { item -> (item == "b") }) == []
  }

  def "test matchesAll"() {
    expect:
    CollectionUtils.matchesAll(["a", "b"], { item -> (item != "") })
    CollectionUtils.matchesAll(null, { item -> (item == "b") })
  }

  def "test matchesAny"() {
    expect:
    CollectionUtils.matchesAny(["a", "b"], { item -> (item == "b") })
    !CollectionUtils.matchesAny(null, { item -> (item == "b") })
  }

  def "test unionAll with empty list"() {
    given:
    List<Integer> list1 = null
    List<Integer> list2 = []

    expect:
    CollectionUtils.unionAll(list1, list2) == []
  }

  def "test unionAll with valid list"() {
    given:
    List<Integer> list1 = [1, 2, 3]
    List<Integer> list2 = []
    List<Integer> list3 = [2, 3 ,4]
    List<Integer> list4 = null
    List<Integer> list5 = [3 ,4]

    expect:
    CollectionUtils.unionAll([list1, list2, list3, list4, list5]) == [1, 2, 3, 2, 3, 4, 3, 4]
  }

  def "test toList"() {
    expect:
    CollectionUtils.toList(null) == []
    CollectionUtils.toList(List.of("a", "b", "c")) == ["a", "b", "c"]
  }

  def "test stream() with collection"() {
    expect:
    StreamUtils.stream(null).collect(toList()) == []
    StreamUtils.stream(List.of("a", "b", "c")).collect(toList()) == ["a", "b", "c"]
  }

  def "test stream() with array"() {
    given:
    String[] strArray = [ "a", "b" ] as String[]
    String[] strArrayNull = null

    expect:
    StreamUtils.stream(strArray).collect(toList()) == ["a", "b"]
    StreamUtils.stream(strArrayNull).collect(toList()) == []
    StreamUtils.stream().collect(toList()) == []
    StreamUtils.stream("a", "b", "c").collect(toList()) == ["a", "b", "c"]
  }

  def "test distinctBy()"() {
    given:
    List<TestItemDto> src = [
        TestItemDto.of("a", "测试项a"),
        TestItemDto.of("b", "测试项b"),
        TestItemDto.of("a", "测试项c"),
        null]

    when:
    List<TestItemDto> dst = CollectionUtils.distinctBy(src, { item -> item.code })

    then:
    dst.size() == 2
    dst[0].code == "a" && dst[0].desc == "测试项a"
    dst[1].code == "b" && dst[1].desc == "测试项b"
  }

  def "test distinctBy Long field"() {
    given:
    List<TestItem2Dto> src = [
        TestItem2Dto.of(1, "测试项a"),
        TestItem2Dto.of(2, "测试项b"),
        TestItem2Dto.of(1, "测试项c"),
        null]

    when:
    List<TestItem2Dto> dst = CollectionUtils.distinctBy(src, { item -> item.code })

    then:
    dst.size() == 2
    dst[0].code == 1 && dst[0].desc == "测试项a"
    dst[1].code == 2 && dst[1].desc == "测试项b"
  }
}
