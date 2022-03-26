package com.tehang.common.utility.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;

/**
 * 表示北京时间，精确到毫秒，格式为yyyy-MM-dd HH:mm:ss.SSS
 */
public final class BjTimeToMS extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  private BjTimeToMS() {
    // 私有的无参构造函数
    super();
  }

  private BjTimeToMS(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_MS);
  }

  public BjTimeToMS(String dateString) {
    super(dateString, DATE_FORMAT_TO_MS);
  }

  // ----------- 其他函数 --------------

  /**
   * 获取北京时间当前时间，精确到毫秒
   */
  public static BjTimeToMS now() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_MS);
    return new BjTimeToMS(dateString);
  }

  public BjTimeToMS plusDays(int days) {
    return new BjTimeToMS(this.innerTime.plusDays(days));
  }

  public BjTimeToMS minusDays(int days) {
    return new BjTimeToMS(this.innerTime.minusDays(days));
  }

  public BjTimeToMS plusSeconds(int seconds) {
    return new BjTimeToMS(this.innerTime.plusSeconds(seconds));
  }

  public BjTimeToMS minusSeconds(int seconds) {
    return new BjTimeToMS(this.innerTime.minusSeconds(seconds));
  }
}
