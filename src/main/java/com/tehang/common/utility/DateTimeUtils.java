package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具类. 采用 {@link java.time.Instant} 和 {@link java.time.LocalDate} 实现.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {
  public static final ZoneId TIMEZONE_BEIJING = ZoneId.of("+08:00");
  public static final ZoneId TIMEZONE_UTC = ZoneId.of("UTC");
  static final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  static final String FLIGHT_TIME_PATTERN = "yyyy-MM-dd HH:mm";
  public static final DateTimeFormatter ISO_INSTANT_FORMATTER = DateTimeFormatter.ofPattern(ISO_PATTERN)
          .withZone(TIMEZONE_UTC);
  public static final DateTimeFormatter DOMESTIC_FLIGHT_INSTANT_FORMATTER = DateTimeFormatter.ofPattern(FLIGHT_TIME_PATTERN)
          .withZone(TIMEZONE_BEIJING);
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Instant 格式化为 String.
   */
  public static String instantToString(Instant instant) {
    return instantToString(instant, ISO_INSTANT_FORMATTER);
  }

  /**
   * String 格式化为 Instant.
   */
  public static Instant instantFromString(String dateTimeString) {
    return instantFromString(dateTimeString, ISO_INSTANT_FORMATTER);
  }

  /**
   * Instant 格式化为 String.
   */
  public static String instantToString(Instant instant, DateTimeFormatter formatter) {
    if (instant == null) {
      return null;
    }

    return formatter.format(instant);
  }

  /**
   * String 格式化为 Instant.
   */
  public static Instant instantFromString(String dateTimeString, DateTimeFormatter formatter) {
    if (StringUtils.isBlank(dateTimeString)) {
      return null;
    }

    Instant result = null;
    try {
      result = formatter.parse(dateTimeString, Instant::from);
    }
    catch (Exception ex) {
      log.error("instantFromString occurred error, dateTimeString: {}, ex: {}", dateTimeString, ex.getMessage(), ex);
    }

    return result;
  }

  /**
   * LocalDate 格式化为 String.
   */
  public static String localDateToString(LocalDate localDate) {
    return DATE_FORMATTER.format(localDate);
  }

  /**
   * String 格式化为 LocalDate.
   */
  public static LocalDate localDateFromString(String dateString) {
    if (StringUtils.isBlank(dateString)) {
      return null;
    }

    LocalDate result = null;
    try {
      result = DATE_FORMATTER.parse(dateString, LocalDate::from);
    }
    catch (Exception ex) {
      log.error("localDateFromString occurred error, dateString: {}, ex: {}", dateString, ex.getMessage(), ex);
    }

    return result;
  }
}
