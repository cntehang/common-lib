package com.tehang.common.utility.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;

/**
 * 表示北京时间，精确到秒，格式为yyyy-MM-dd HH:mm:ss
 */
public final class BjTimeToSecond extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  private BjTimeToSecond() {
    // 私有的无参构造函数
    super();
  }

  private BjTimeToSecond(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_SECOND);
  }

  public BjTimeToSecond(String dateString) {
    super(dateString, DATE_FORMAT_TO_SECOND);
  }

  // ----------- 其他函数 --------------

  /**
   * 获取北京时间当前时间，精确到秒
   */
  public static BjTimeToSecond now() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_SECOND);
    return new BjTimeToSecond(dateString);
  }

  public BjTimeToSecond plusDays(int days) {
    return new BjTimeToSecond(this.innerTime.plusDays(days));
  }

  public BjTimeToSecond minusDays(int days) {
    return new BjTimeToSecond(this.innerTime.minusDays(days));
  }

  public BjTimeToSecond plusSeconds(int seconds) {
    return new BjTimeToSecond(this.innerTime.plusSeconds(seconds));
  }

  public BjTimeToSecond minusSeconds(int seconds) {
    return new BjTimeToSecond(this.innerTime.minusSeconds(seconds));
  }
}
