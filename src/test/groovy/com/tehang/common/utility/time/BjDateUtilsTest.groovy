package com.tehang.common.utility.time

import com.tehang.common.TestSpecification
import com.tehang.common.utility.BjDateUtils

class BjDateUtilsTest extends TestSpecification {

  def "calculateAge test"() {
    expect:
    BjDateUtils.calculateAge(new BjDate(startDate), new BjDate(endDate)) == years

    where:
    startDate    | endDate      | years
    '2019-01-01' | '2019-01-02' | 0
    '1949-05-01' | '2019-05-02' | 70
    '1949-05-01' | '1950-05-02' | 1
    '1986-05-04' | '1986-05-05' | 0
    '1987-04-12' | '1987-05-12' | 0
    '1989-04-16' | '1991-04-14' | 1
    '1990-04-15' | '1990-05-15' | 0
    '1991-04-14' | '1991-09-15' | 0
    '1993-02-28' | '1994-02-28' | 1
    '1992-02-28' | '1993-02-27' | 0
    '1992-02-28' | '1993-02-28' | 1
    '1992-02-29' | '1993-02-28' | 0
    '1992-02-29' | '1993-03-01' | 1
    '1992-02-29' | '1996-02-28' | 3
    '1992-02-29' | '1996-02-29' | 4
    '2009-10-14' | '2021-10-13' | 11
    '2009-10-13' | '2021-10-13' | 12
    '2019-10-13' | '2021-10-13' | 2
    '2019-10-14' | '2021-10-13' | 1
  }

  def "daysBetween test"() {
    expect:
    BjDateUtils.daysBetween(new BjDate(startDate), new BjDate(endDate)) == days

    where:
    startDate    | endDate      | days
    '2019-01-01' | '2019-01-02' | 1
    '2019-01-01' | '2019-01-01' | 0
    '2019-01-02' | '2019-01-01' | -1
  }
}
