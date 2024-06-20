package com.tehang.common.utility.db

import com.tehang.common.TestSpecification

/** 生成分页的countSql的测试类 */
class CountSqlBuilderSpec extends TestSpecification {

  def "test1.1: buildCountSql test"() {
    expect:
    CountSqlBuilder.buildCountSql("select a, b, c from table1 where true") == "select count(*) from table1 where true"

    CountSqlBuilder.buildCountSql("select a, b, (select d,e from table2) as d from table1 where true") == "select count(*) from table1 where true"

    CountSqlBuilder.buildCountSql("select a, b, (select d,e from table2) as d from table1 where exist (select * from table3)") == "select count(*) from table1 where exist (select * from table3)"
  }
}
