package com.tehang.common.utility

import com.tehang.common.TestSpecification
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode


class JsonUtilsTest extends TestSpecification {

  JsonObject expectedObject = new JsonObject()
  String expectedJsonStr // = "{\"stringValue\":\"this is string value\",\"intValue\":100,\"boolValue\":false}"

  def setup() {
    expectedObject.stringValue = "this is string value"
    expectedObject.intValue = 100
    expectedObject.boolValue = false
    expectedJsonStr = TestSpecification.jsonOutput.toJson(expectedObject)

  }

  def "test1: Object to string "() {
    when:

    String actualJson = JsonUtils.toJson(expectedObject)
    then:
    JSONAssert.assertEquals(expectedJsonStr, actualJson, JSONCompareMode.STRICT)
  }

  def "test2: String to object"() {
    when:

    JsonObject actualObject = new JsonObject(TestSpecification.jsonSlurper.parse(new FileReader('src/test/resources/fake_data/jsonUtils.json')))
    then:
    assert expectedObject == actualObject
  }
}
