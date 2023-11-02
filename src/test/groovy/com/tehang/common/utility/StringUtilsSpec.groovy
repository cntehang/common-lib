package com.tehang.common.utility

import com.tehang.common.TestSpecification

class StringUtilsSpec extends TestSpecification {

  // ------------ 判空相关的方法 ----------

  def "test isBlank "() {
    expect:
    StringUtils.isBlank(null)
    StringUtils.isBlank("")
    StringUtils.isBlank(" ")
    !StringUtils.isBlank("a")
  }

  def "test isNotBlank "() {
    expect:
    !StringUtils.isNotBlank(null)
    !StringUtils.isNotBlank("")
    !StringUtils.isNotBlank(" ")
    StringUtils.isNotBlank("a")
  }

  def "test isEmpty "() {
    expect:
    StringUtils.isEmpty(null)
    StringUtils.isEmpty("")
    !StringUtils.isEmpty("a")
  }

  def "test isNotEmpty "() {
    expect:
    !StringUtils.isNotEmpty(null)
    !StringUtils.isNotEmpty("")
    StringUtils.isNotEmpty("a")
  }

  // ------------ 判等相关的方法 ----------

  def "test equals "() {
    expect:
    StringUtils.equals(null, null)
    StringUtils.equals("", "")
    StringUtils.equals("abc", "abc")
    !StringUtils.equals(null, "")
    !StringUtils.equals("abc", "a")
    !StringUtils.equals("abc", "ABC")
  }

  def "test equalsIgnoreCase "() {
    expect:
    StringUtils.equalsIgnoreCase(null, null)
    StringUtils.equalsIgnoreCase("", "")
    StringUtils.equalsIgnoreCase("abc", "abc")
    StringUtils.equalsIgnoreCase("abc", "ABC")
    !StringUtils.equalsIgnoreCase(null, "")
    !StringUtils.equalsIgnoreCase("abc", "a")
  }

  def "test equalsAny with array "() {
    expect:
    StringUtils.equalsAny("a", "a", "b", "c")
    !StringUtils.equalsAny("a", "b", "c")
  }

  def "test equalsAny with list "() {
    expect:
    StringUtils.equalsAny("a", List.of("a", "b", "c"))
    !StringUtils.equalsAny("a", List.of("b", "c"))
  }

  def "test equalsAnyIgnoreCase with array "() {
    expect:
    StringUtils.equalsAnyIgnoreCase("a", "a", "b", "c")
    StringUtils.equalsAnyIgnoreCase("a", "A", "b", "c")
    !StringUtils.equalsAnyIgnoreCase("a", "b", "c")
  }

  def "test equalsAnyIgnoreCase with list "() {
    expect:
    StringUtils.equalsAnyIgnoreCase("a", List.of("a", "b", "c"))
    StringUtils.equalsAnyIgnoreCase("a", List.of("A", "b", "c"))
    !StringUtils.equalsAnyIgnoreCase("a", List.of("b", "c"))
  }

  // ------------ split相关的方法 ----------

  def "test splitByComma"() {
    expect:
    StringUtils.splitByComma("a,b,c") == List.of("a", "b", "c")
    StringUtils.splitByComma(null) == null
  }

  def "test splitBy"() {
    expect:
    StringUtils.splitBy("a;b;c", ";") == List.of("a", "b", "c")
    StringUtils.splitBy(null, ";") == null
  }

// ------------ join相关的方法 ----------

  def "test joinWithComma"() {
    expect:
    StringUtils.joinWithComma("a", "b", "c") == "a,b,c"
    StringUtils.joinWithComma(List.of("a", "b", "c")) == "a,b,c"
    StringUtils.joinWithComma(null) == ""
  }

  def "test joinWith"() {
    expect:
    StringUtils.joinWith(",", "a", "b", "c") == "a,b,c"
    StringUtils.joinWith(",", List.of("a", "b", "c")) == "a,b,c"
    StringUtils.joinWith(",", null) == ""
  }

  // ------------ 获取子串相关的方法 ----------

  def "test left"() {
    expect:
    StringUtils.left("abc", 2) == "ab"
    StringUtils.left("abc", 4) == "abc"
    StringUtils.left("abc", 0) == ""
    StringUtils.left("abc", -1) == ""
    StringUtils.left(null, 0) == null
  }

  // ------------ 子串匹配相关的方法 ----------

  def "test contains"() {
    expect:
    StringUtils.contains("abc", "a")
    StringUtils.contains("abc", "")
    !StringUtils.contains("abc", "A")
    !StringUtils.contains("abc", null)
  }

  def "test containsIgnoreCase"() {
    expect:
    StringUtils.containsIgnoreCase("abc", "a")
    StringUtils.containsIgnoreCase("abc", "A")
  }

  def "test startsWith"() {
    expect:
    StringUtils.startsWith("abc", "a")
    !StringUtils.startsWith("abc", "A")
    StringUtils.startsWith(null, null)
  }

  def "test startsWithIgnoreCase"() {
    expect:
    StringUtils.startsWithIgnoreCase("abc", "a")
    StringUtils.startsWithIgnoreCase("abc", "A")
    StringUtils.startsWithIgnoreCase(null, null)
  }

  def "test endsWith"() {
    expect:
    StringUtils.endsWith("abc", "c")
    !StringUtils.endsWith("abc", "C")
    StringUtils.endsWith(null, null)
  }

  def "test endsWithIgnoreCase"() {
    expect:
    StringUtils.endsWithIgnoreCase("abc", "c")
    StringUtils.endsWithIgnoreCase("abc", "C")
    StringUtils.endsWithIgnoreCase(null, null)
  }

  // ------------ trim相关的方法 ----------

  def "test trim"() {
    expect:
    StringUtils.trim(null) == null
    StringUtils.trim(" abc ") == "abc"
    StringUtils.trim(" a b c ") == "a b c"
  }

  def "test trimToEmpty"() {
    expect:
    StringUtils.trimToEmpty(null) == ""
    StringUtils.trimToEmpty(" abc ") == "abc"
    StringUtils.trimToEmpty(" a b c ") == "a b c"
  }

  // ------------ stringOf相关的方法 ----------

  def "test stringOf"() {
    expect:
    StringUtils.stringOf(null) == null
    StringUtils.stringOf("") == ""
    StringUtils.stringOf("abc") == "abc"
    StringUtils.stringOf(123) == "123"
    StringUtils.stringOf(123l) == "123"
    StringUtils.stringOf(123.45f) == "123.45"
    StringUtils.stringOf(123.45d) == "123.45"
    StringUtils.stringOf(true) == "true"
    StringUtils.stringOf(false) == "false"
  }

  // ------------ string列表相关的方法 ----------
  def "test concat"() {
    expect:
    StringUtils.concat(null) == List.of()
    StringUtils.concat(List.of("a", "b"), List.of("c", "d")) == List.of("a", "b", "c", "d")
    StringUtils.concat(List.of("a", "b", ""), List.of("b", "c", "")) == List.of("a", "b", "", "b", "c", "")
  }
}
