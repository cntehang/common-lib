package com.tehang.common.utility.time;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.Days;

import java.io.Serializable;

/**
 * 表示一个时间段，包含开始和截止日期。时区为北京时间，精确到天。
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class DateRange implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  /**
   * 开始日期, null表示开始日期无限制。
   */
  @ApiModelProperty(value = "开始日期, null表示开始日期无限制", dataType = "java.lang.String", example = "2022-04-15")
  private BjDate from;

  /**
   * 结束日期, null表示结束日期无限制。该时间段为闭区间，包含开始和结束的时间点。
   */
  @ApiModelProperty(value = "结束日期, null表示结束日期无限制。该时间段为闭区间，包含开始和结束的时间点", dataType = "java.lang.String", example = "2022-04-18")
  private BjDate to;

  // ----------- 构造函数 --------------

  private DateRange() {
  }

  public DateRange(BjDate dateFrom, BjDate dateTo) {
    if (dateFrom != null && dateTo != null) {
      if (dateFrom.isAfter(dateTo)) {
        this.from = dateTo;
        this.to = dateFrom;
        return;
      }
    }

    this.from = dateFrom;
    this.to = dateTo;
  }

  // ----------- 其他函数 --------------

  /**
   * 创建一个时间段对象，参数可以为null, 表示无限制。
   */
  public static DateRange create(BjDate dateFrom, BjDate dateTo) {
    return new DateRange(dateFrom, dateTo);
  }

  /**
   * 当前时间段是否包含指定的日期？
   */
  public boolean contains(BjDate date) {
    if (date == null) {
      throw new IllegalArgumentException("date can not be null");
    }
    return (from == null || from.compareTo(date) <= 0)
        && (to == null || to.compareTo(date) >= 0);
  }

  /**
   * 当前的时间段是否和指定的时间段有重合？
   */
  public boolean overlapped(DateRange range) {
    return isBeforeOrEqual(this.from, range.to)
        && isBeforeOrEqual(range.from, this.to);
  }

  private static boolean isBeforeOrEqual(BjDate from, BjDate to) {
    if (from == null) {
      return true;
    }
    if (to == null) {
      return true;
    }
    return from.compareTo(to) <= 0;
  }

  /**
   * 计算当前时间段包含了多少天(包含截止的那一天)？
   */
  public int totalDays() {
    if (from == null) {
      throw new UnsupportedOperationException("totalDays not supported, from is null");
    }
    if (to == null) {
      throw new UnsupportedOperationException("totalDays not supported, to is null");
    }
    int days = Days.daysBetween(from.innerTime, to.innerTime).getDays();
    // totalDays包含结束的那一天
    return days + 1;
  }

  // ----------- toString --------------
  @Override
  public String toString() {
    return String.format("(%s, %s)", from, to);
  }
}
