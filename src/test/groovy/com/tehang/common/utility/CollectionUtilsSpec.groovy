package com.tehang.common.utility


import com.tehang.common.TestSpecification

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
}
