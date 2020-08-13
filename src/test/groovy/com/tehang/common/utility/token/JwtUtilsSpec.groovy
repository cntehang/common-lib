package com.tehang.common.utility.token

import com.tehang.common.TestSpecification

class JwtUtilsSpec extends TestSpecification {

  InnerJwtPayload payload
  def secret = "my-secret"

  def setup() {
    payload = InnerJwtPayload.create(10001L, "IOS", true, 20002L, "tester")

  }


  def "test1: create token"() {
    when:

    def token = JwtUtils.createToken(payload, secret, "tester", "testcase")
    println(token)
    then:
    noExceptionThrown()
    token != null
  }


  def "test2: fetch payload"() {
    when:
    def tokenStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJ0ZXN0Y2FzZSIsImlzcyI6InRlc3RlciIsImlhdCI6MTU3Mjk0OTQ5MCwiQ3VzdG9tZXJQYXlsb2FkIjoie1wiZW1wbG95ZWVJZFwiOjEwMDAxLFwiY2xpZW50VHlwZVwiOlwiSU9TXCIsXCJhZG1pblwiOnRydWUsXCJzdGFmZklkXCI6MjAwMDIsXCJzdGFmZk5hbWVcIjpcInRlc3RlclwifSJ9.2PI-zfX7l_08UBxUCakaxbhPsgjdqf4IUYmumRBJLg8"
    def payload1 = JwtUtils.getPayload(tokenStr, secret)
    then:
    noExceptionThrown()
    payload1 == payload
  }
}
