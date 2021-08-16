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
  static final ZoneId TIMEZONE_BEIJING = ZoneId.of("+08:00");
  static final ZoneId TIMEZONE_UTC = ZoneId.of("UTC");
  static final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ofPattern(ISO_PATTERN)
          .withZone(TIMEZONE_UTC);
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Instant 格式化为 String.
   */
  public static String instantToString(Instant instant) {
    return INSTANT_FORMATTER.format(instant);
  }

  /**
   * String 格式化为 Instant.
   */
  public static Instant instantFromString(String dateTimeString) {
    if (StringUtils.isBlank(dateTimeString)) {
      return null;
    }

    Instant result = null;
    try {
      result = INSTANT_FORMATTER.parse(dateTimeString, Instant::from);
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
