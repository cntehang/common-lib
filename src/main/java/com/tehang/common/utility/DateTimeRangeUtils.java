package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.time.LocalDate;

/**
 * 日期范围工具类. 采用 {@link java.time.Instant} 和 {@link java.time.LocalDate} 实现.
 * <p>
 * 整体采用左闭右开原则
 * <p>
 * 左侧日期的 0:00，后侧日期是次日的 0:00 但不包含（或者说后侧时间的 23:59）
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeRangeUtils {
  /**
   * 根据 LocalDate 获取 Instant
   */
  public static Instant instantFromDateRangeStart(LocalDate rangeStart) {
    assert rangeStart != null;

    return rangeStart.atStartOfDay(DateTimeUtils.TIMEZONE_BEIJING).toInstant();
  }

  /**
   * 根据 LocalDate 获取 Instant
   */
  public static Instant instantFromDateRangeEnd(LocalDate rangeEnd) {
    assert rangeEnd != null;

    return rangeEnd.plusDays(1).atStartOfDay(DateTimeUtils.TIMEZONE_BEIJING).toInstant();
  }

  /**
   * 根据 LocalDate 范围获取 Instant 范围
   */
  public static Pair<Instant, Instant> instantRangeFromDateRange(LocalDate rangeStart, LocalDate rangeEnd) {
    assert rangeStart != null;
    assert rangeEnd != null;
    assert !rangeStart.isAfter(rangeEnd);

    return Pair.of(instantFromDateRangeStart(rangeStart),
            instantFromDateRangeEnd(rangeEnd));
  }

  /**
   * 根据 LocalDate 获取 Instant String
   */
  public static String instantStringFromDateRangeStart(LocalDate rangeStart) {
    return DateTimeUtils.instantToString(instantFromDateRangeStart(rangeStart));
  }

  /**
   * 根据 LocalDate 获取 Instant String
   */
  public static String instantStringFromDateRangeEnd(LocalDate rangeEnd) {
    return DateTimeUtils.instantToString(instantFromDateRangeEnd(rangeEnd));
  }

  /**
   * 根据 LocalDate 范围获取 Instant String 范围
   */
  public static Pair<String, String> instantStringRangeFromDateRange(LocalDate rangeStart, LocalDate rangeEnd) {
    final Pair<Instant, Instant> instantRange = instantRangeFromDateRange(rangeStart, rangeEnd);

    return Pair.of(DateTimeUtils.instantToString(instantRange.getLeft()),
            DateTimeUtils.instantToString(instantRange.getRight()));
  }
}
