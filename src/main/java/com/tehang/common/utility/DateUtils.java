package com.tehang.common.utility;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 日期工具类.
 * 采用joda工具包实现.
 */
public final class DateUtils {

  /**
   * 所有服务的统一格式
   */
  private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(PATTERN);

  private DateUtils() {
    // do nothing
  }

  /**
   * get current date time in string.
   *
   * @return
   */
  public static String now() {
    return DateTime.now().toInstant().toString(DATE_TIME_FORMATTER);
  }

  /**
   * get current date time in date
   *
   * @return
   */
  public static Date nowInDate() {
    return DateTime.now().toInstant().toDate();
  }

  /**
   * 后延的时间
   *
   * @param seconds 秒
   * @return
   */
  public static String timeWithDuration(long seconds) {
    return DateTime.now().toInstant().plus(seconds * 1000).toString(DATE_TIME_FORMATTER);
  }

  /**
   * get date time from string value.
   *
   * @param value
   * @return
   */
  public static DateTime from(String value) {
    return DateTime.parse(value, DATE_TIME_FORMATTER);
  }

  /**
   * 是否比当前时间早.
   * 注意，这里都是按照UTC时间进行比较
   *
   * @param value
   * @return
   */
  public static boolean isBeforeNow(String value) {
    return value.compareTo(now()) == -1;
  }
}
