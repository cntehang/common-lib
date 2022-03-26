package com.tehang.common.utility.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;

/**
 * 表示北京时间，精确到分钟，格式为yyyy-MM-dd HH:mm
 */
public final class BjTimeToMinute extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  private BjTimeToMinute() {
    // 私有的无参构造函数
    super();
  }

  private BjTimeToMinute(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_MINUTE);
  }

  public BjTimeToMinute(String dateString) {
    super(dateString, DATE_FORMAT_TO_MINUTE);
  }

  // ----------- 其他函数 --------------

  /**
   * 获取北京时间当前时间，精确到分钟
   */
  public static BjTimeToMinute now() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_MINUTE);
    return new BjTimeToMinute(dateString);
  }

  public BjTimeToMinute plusDays(int days) {
    return new BjTimeToMinute(this.innerTime.plusDays(days));
  }

  public BjTimeToMinute minusDays(int days) {
    return new BjTimeToMinute(this.innerTime.minusDays(days));
  }

  public BjTimeToMinute plusMinutes(int minutes) {
    return new BjTimeToMinute(this.innerTime.plusMinutes(minutes));
  }

  public BjTimeToMinute minusMinutes(int minutes) {
    return new BjTimeToMinute(this.innerTime.minusMinutes(minutes));
  }
}
