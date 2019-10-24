package com.tehang.common.infrastructure.generator

import com.tehang.common.TestSpecification
import com.tehang.common.utility.generator.IdGenerator


class IdGeneratorSpec extends TestSpecification {

  def "测试1： 生成正确的ID"() {
    when: "初始化generator，并生成一个ID"
    IdGenerator.initInstance(1, 1)
    long id = IdGenerator.nextId()
    then:
    id > 0
  }
}
