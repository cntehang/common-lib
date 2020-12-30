package com.tehang.common.utility

import com.tehang.common.TestSpecification

class ThCollectionUtilsSpec extends TestSpecification {

  def "测试1： 无效的元素"() {
    given:
    List<Integer> list1 = null
    List<Integer> list2 = []
    List<Integer> list3 = [null]

    when:
    List<Integer> result = ThCollectionUtils.unionToList(list1, list2, list3)

    then:
    result == []
  }

  def "测试2： 正常元素"() {
    given:
    List<Integer> list1 = [1, 2, 3]
    List<Integer> list2 = []
    List<Integer> list3 = [2, 3 ,4]
    List<Integer> list4 = [3 ,4]

    when:
    List<Integer> result = ThCollectionUtils.unionToList(list1, list2, list3, list4)

    then:
    result == [1, 2, 3, 2, 3, 4, 3, 4]
  }


}
