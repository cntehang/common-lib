package com.tehang.common.infrastructure.generator

import com.tehang.common.TestSpecification
import com.tehang.common.utility.generator.IdGenerator
import com.tehang.common.utility.generator.IdGeneratorConfig
import spock.lang.Shared


class IdGeneratorSpec extends TestSpecification {
  IdGeneratorConfig idGeneratorConfig = Mock(IdGeneratorConfig)
  IdGenerator idGenerator = new IdGenerator(idGeneratorConfig)

  def setup() {
    idGeneratorConfig.getDataCenterId() >> 1
    idGeneratorConfig.getMachineId() >> 1

    idGenerator.init()
  }

  def "测试1： 生成正确的ID"() {
    when: "初始化generator，并生成一个ID"
    long id = IdGenerator.nextId()
    then:
    id > 0
  }
}
