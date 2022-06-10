package com.tehang.common.utility.time;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 表示一个时间段，包含开始和截止时间，但不包含日期。时区为北京时间，精确到秒。
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class TimeRange implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  /**
   * 时间格式: HH:mm:ss, 精确到秒
   */
  public static final String TIME_FORMAT_TO_SECOND = "HH:mm:ss";

  /**
   * 时间格式: HH:mm:ss 的正则表达式
   */
  public static final String TIME_FORMAT_TO_SECOND_REGEX = "^(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])";

  /**
   * 开始时间, 格式为HH:mm:ss, null表示开始时间无限制。
   */
  @ApiModelProperty(value = "开始时间, 格式为HH:mm:ss, null表示开始时间无限制", example = "10:05:10")
  @Column(columnDefinition = "VARCHAR(8) NULL")
  private String from;

  /**
   * 结束时间, 格式为HH:mm:ss, null表示结束时间无限制。该时间段为闭区间，包含开始和结束的时间点
   */
  @ApiModelProperty(value = "结束时间, 格式为HH:mm:ss, null表示结束时间无限制。该时间段为闭区间，包含开始和结束的时间点", example = "10:05:30")
  @Column(columnDefinition = "VARCHAR(8) NULL")
  private String to;

  // ----------- 构造函数 --------------

  protected TimeRange() {
    // 保留此空构造函数，以方便一些框架使用
  }

  public TimeRange(String timeFrom, String timeTo) {
    this.from = normalizeTimeFormat(timeFrom);
    this.to = normalizeTimeFormat(timeTo);

    if (isNotBlank(from) && isNotBlank(to) && from.compareTo(to) > 0) {
      throw new IllegalArgumentException(String.format("无效的时间范围，开始时间[%s]不能大于结束时间[%s]", timeFrom, timeTo));
    }
  }

  private static String normalizeTimeFormat(String timeStr) {
    if (isBlank(timeStr)) {
      return null;
    }
    if (!timeStr.matches(TIME_FORMAT_TO_SECOND_REGEX)) {
      throw new IllegalArgumentException(String.format("无效的时间字符串: %s, 应为%s格式", timeStr, TIME_FORMAT_TO_SECOND));
    }
    return timeStr;
  }

  // ----------- 其他函数 --------------

  /**
   * 创建一个时间段对象，参数可以为null, 表示无限制。
   */
  public static TimeRange create(String timeFrom, String timeTo) {
    return new TimeRange(timeFrom, timeTo);
  }

  /**
   * 创建一个时间段对象，参数可以为null, 表示无限制。
   */
  public static TimeRange create(BjDateTime timeFrom, BjDateTime timeTo) {
    String timeFromStr = timeFrom == null ? null : timeFrom.getInnerTime().toString(TIME_FORMAT_TO_SECOND);
    String timeToStr = timeTo == null ? null : timeTo.getInnerTime().toString(TIME_FORMAT_TO_SECOND);

    return new TimeRange(timeFromStr, timeToStr);
  }

  /**
   * 当前时间段是否包含指定的时间？
   */
  public boolean contains(String time) {
    if (isBlank(time)) {
      throw new IllegalArgumentException("时间参数不能为空");
    }
    if (!time.matches(TIME_FORMAT_TO_SECOND_REGEX)) {
      throw new IllegalArgumentException(String.format("无效的时间字符串: %s, 应为%s格式", time, TIME_FORMAT_TO_SECOND));
    }

    return (from == null || from.compareTo(time) <= 0)
        && (to == null || to.compareTo(time) >= 0);
  }

  /**
   * 当前时间段是否包含指定的时间？
   */
  public boolean contains(BjDateTime time) {
    if (time == null) {
      throw new IllegalArgumentException("时间参数不能为空");
    }
    return contains(time.getInnerTime().toString(TIME_FORMAT_TO_SECOND));
  }

  /**
   * 当前的时间段是否和指定的时间段有重合？
   */
  public boolean overlapped(TimeRange range) {
    return isBeforeOrEqual(this.from, range.to)
        && isBeforeOrEqual(range.from, this.to);
  }

  private static boolean isBeforeOrEqual(String from, String to) {
    if (isBlank(from)) {
      return true;
    }
    if (isBlank(to)) {
      return true;
    }
    return from.compareTo(to) <= 0;
  }

  // ----------- toString --------------
  @Override
  public String toString() {
    return String.format("(%s, %s)", from, to);
  }
}
