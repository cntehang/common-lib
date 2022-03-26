package com.tehang.common.utility.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;

/**
 * 表示北京时间，精确到天，格式为yyyy-MM-dd。
 */
public final class BjDate extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  private BjDate() {
    // 私有的无参构造函数
    super();
  }

  private BjDate(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_DAY);
  }

  public BjDate(String dateString) {
    super(dateString, DATE_FORMAT_TO_DAY);
  }

  // ----------- 其他函数 --------------

  /**
   * 获取北京时间当前的日期，精确到天
   */
  public static BjDate today() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_DAY);
    return new BjDate(dateString);
  }

  public BjDate plusDays(int days) {
    return new BjDate(this.innerTime.plusDays(days));
  }

  public BjDate minusDays(int days) {
    return new BjDate(this.innerTime.minusDays(days));
  }

}
