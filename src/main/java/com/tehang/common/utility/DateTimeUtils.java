package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

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
  private static final ZoneId TIMEZONE_BEIJING = ZoneId.of("+08:00");
  private static final ZoneId TIMEZONE_UTC = ZoneId.of("UTC");
  private static final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ofPattern(ISO_PATTERN)
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

  /**
   * 根据 LocalDate 范围获取 Instant 范围
   */
  public static Pair<Instant, Instant> instantRangeFromDateRange(LocalDate rangeStart, LocalDate rangeEnd) {
    assert rangeStart != null;
    assert rangeEnd != null;
    assert !rangeStart.isAfter(rangeEnd);

    return Pair.of(rangeStart.atStartOfDay(TIMEZONE_BEIJING).toInstant(),
            rangeEnd.plusDays(1).atStartOfDay(TIMEZONE_BEIJING).toInstant());
  }

  /**
   * 根据 LocalDate 范围获取 Instant String 范围
   */
  public static Pair<String, String> instantStringRangeFromDateRange(LocalDate rangeStart, LocalDate rangeEnd) {
    final Pair<Instant, Instant> instantRange = instantRangeFromDateRange(rangeStart, rangeEnd);

    return Pair.of(instantToString(instantRange.getLeft()),
            instantToString(instantRange.getRight()));
  }
}
