package com.tehang.common.utility

import com.tehang.common.TestSpecification
import com.tehang.common.utility.token.InnerJwtPayload
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode


class JsonUtilsSpec extends TestSpecification {
  class JsonObject {

    String stringValue
    int intValue
    boolean boolValue

    boolean equals(JsonObject valueToCompare) {
      return stringValue == valueToCompare.stringValue &&
              intValue == valueToCompare.intValue &&
              boolValue == valueToCompare.boolValue
    }
  }


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

  def "test3: 反序列化jwt对象时能兼容不认识的字段"() {
    when:
    def reader  = new FileReader('src/test/resources/fake_data/jwtUtils.json')
    String json = reader.getText()
    InnerJwtPayload jwtPayload = InnerJwtPayload.fromJson(json)

    then:
    //json中有个extra字段，但实体类中没有，仍然能够反序列化
    jwtPayload.employeeId == 200001
    jwtPayload.staffId == 100001
    jwtPayload.approvalNo == "10001"
  }
}
