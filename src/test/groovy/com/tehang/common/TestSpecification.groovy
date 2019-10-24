package com.tehang.common

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.lang.Specification

class TestSpecification extends Specification {
  /**
   * json 工具类
   */
  static def jsonSlurper = new JsonSlurper()
  static def jsonOutput = new JsonOutput()
  static def xmlSlurper = new XmlSlurper()


}
