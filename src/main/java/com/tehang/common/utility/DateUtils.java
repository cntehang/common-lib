package com.tehang.common.utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
  private static final ZoneId TIMEZONE_UTC = ZoneId.of("UTC");
  private static final ZoneId TIMEZONE_BEIJING = ZoneId.of("+08:00");
  private static final java.time.format.DateTimeFormatter JAVA_DATE_TIME_FORMATTER = java.time.format.DateTimeFormatter.ofPattern(PATTERN).withZone(TIMEZONE_UTC);


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

  public static String now(String pattern) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
    return DateTime.now().toInstant().toString(formatter);
  }

  /**
   * 获取相应格式以及相应时差的时间值
   *
   * @param pattern 格式
   * @param seconds 时差，单位秒
   * @param zone    时区
   * @return
   */
  public static String time(String pattern, long seconds, DateTimeZone zone) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
    return DateTime.now(zone).toInstant().plus(seconds * 1000).toString(formatter);
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


  /**
   * LocalDateTime 格式化为 String
   */
  public static String localDateTimeToString(LocalDateTime dateTime) {
    return JAVA_DATE_TIME_FORMATTER.format(dateTime.atZone(TIMEZONE_BEIJING).withZoneSameInstant(TIMEZONE_UTC));
  }

  /**
   * String 格式化为 LocalDateTime
   */
  public static LocalDateTime localDateTimeFromString(String dateTimeString) {
    return LocalDateTime.parse(dateTimeString, JAVA_DATE_TIME_FORMATTER).atZone(TIMEZONE_UTC).withZoneSameInstant(TIMEZONE_BEIJING).toLocalDateTime();
  }

  /**
   * Instant 格式化为 String
   */
  public static String instantToString(Instant instant) {
    return JAVA_DATE_TIME_FORMATTER.format(instant);
  }

  /**
   * String 格式化为 Instant
   */
  public static Instant instantFromString(String dateTimeString) {
    return JAVA_DATE_TIME_FORMATTER.parse(dateTimeString, Instant::from);
  }
}
