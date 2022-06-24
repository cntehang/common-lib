package com.tehang.common.utility.time;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 表示北京时间，根据精度的不同，有不同的格式。可以精确到天，分钟，秒和毫秒。
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode
public abstract class BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ------------- 日期相关的常量定义 ---------------
  /**
   * 北京/上海时间
   */
  public static final String ZONE_SHANGHAI = "+08:00";

  /**
   * 日期格式: yyyy-MM-dd, 精确到天
   */
  public static final String DATE_FORMAT_TO_DAY = "yyyy-MM-dd";

  /**
   * 日期格式: yyyy-MM-dd 的正则表达式
   */
  public static final String DATE_FORMAT_TO_DAY_REGEX = "(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-"
          + "(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}"
          + "(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)";

  /**
   * 日期格式: yyyy-MM-dd HH:mm, 精确到分钟
   */
  public static final String DATE_FORMAT_TO_MINUTE = "yyyy-MM-dd HH:mm";

  /**
   * 日期格式: yyyy-MM-dd HH:mm 的正则表达式
   */
  public static final String DATE_FORMAT_TO_MINUTE_REGEX = "^((((19|20)\\d{2})-(0?[13-9]|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)"
          + "|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29)) "
          + "(2[0-3]|[01][0-9]):([0-5][0-9])";

  /**
   * 日期格式: yyyy-MM-dd HH:mm:ss, 精确到秒
   */
  public static final String DATE_FORMAT_TO_SECOND = "yyyy-MM-dd HH:mm:ss";

  /**
   * 日期格式: yyyy-MM-dd HH:mm:ss 的正则表达式
   */
  public static final String DATE_FORMAT_TO_SECOND_REGEX = DATE_FORMAT_TO_MINUTE_REGEX + ":([0-5][0-9])";

  /**
   * 日期格式: yyyy-MM-dd HH:mm:ss.SSS, 精确到毫秒
   */
  public static final String DATE_FORMAT_TO_MS = "yyyy-MM-dd HH:mm:ss.SSS";

  /**
   * 日期格式: yyyy-MM-dd HH:mm:ss.SSS 的正则表达式
   */
  public static final String DATE_FORMAT_TO_MS_REGEX = DATE_FORMAT_TO_SECOND_REGEX + "(\\.[0-9]{3})";

  // ----------- 字段 ------------
  /**
   * 内部记录的时间值，北京时区。
   */
  protected DateTime innerTime;

  /**
   * 格式化字符串
   */
  protected String format;

  // ----------- 构造函数 --------------
  protected BjDateTime() {
    // 私有的无参构造函数
  }

  protected BjDateTime(DateTime innerTime, String dateFormat) {
    this.innerTime = innerTime;
    this.format = dateFormat;
  }

  protected BjDateTime(String dateString, String dateFormat) {
    // 验证时间字符串和格式
    validate(dateString, dateFormat);

    this.innerTime = parseInBeijing(dateString);
    this.format = dateFormat;
  }

  private static void validate(String dateString, String dateFormat) {
    if (isBlank(dateString)) {
      throw new IllegalArgumentException("时间字符串不能为空");
    }
    if (isBlank(dateFormat)) {
      throw new IllegalArgumentException("时间格式不能为空");
    }

    if (StringUtils.equals(dateFormat, DATE_FORMAT_TO_DAY)) {
      if (!dateString.matches(DATE_FORMAT_TO_DAY_REGEX)) {
        throw new IllegalArgumentException(String.format("无效的时间字符串: %s, 应为%s格式", dateString, DATE_FORMAT_TO_DAY));
      }
    }
    else if (StringUtils.equals(dateFormat, DATE_FORMAT_TO_MINUTE)) {
      if (!dateString.matches(DATE_FORMAT_TO_MINUTE_REGEX)) {
        throw new IllegalArgumentException(String.format("无效的时间字符串: %s, 应为%s格式", dateString, DATE_FORMAT_TO_MINUTE));
      }
    }
    else if (StringUtils.equals(dateFormat, DATE_FORMAT_TO_SECOND)) {
      if (!dateString.matches(DATE_FORMAT_TO_SECOND_REGEX)) {
        throw new IllegalArgumentException(String.format("无效的时间字符串: %s, 应为%s格式", dateString, DATE_FORMAT_TO_SECOND));
      }
    }
    else if (StringUtils.equals(dateFormat, DATE_FORMAT_TO_MS)) {
      if (!dateString.matches(DATE_FORMAT_TO_MS_REGEX)) {
        throw new IllegalArgumentException(String.format("无效的时间字符串: %s, 应为%s格式", dateString, DATE_FORMAT_TO_MS));
      }
    }
    else {
      throw new IllegalArgumentException("无效的时间格式: " + dateFormat);
    }
  }

  private static DateTime parseInBeijing(String dateString) {
    String value2 = StringUtils.replace(dateString, StringUtils.SPACE, "T");
    return new DateTime(value2, DateTimeZone.forID(ZONE_SHANGHAI));
  }

  // ----------- 其他函数 --------------
  /**
   * 当前时间是否在指定的时间之后？
   */
  public boolean isAfter(@NotNull BjDateTime bjDateTime) {
    if (bjDateTime == null) {
      throw new IllegalArgumentException("argument must not be null");
    }
    return compareTo(bjDateTime) > 0;
  }

  /**
   * 当前时间是否在指定的时间之后或相等？
   */
  public boolean isAfterOrEqual(@NotNull BjDateTime bjDateTime) {
    if (bjDateTime == null) {
      throw new IllegalArgumentException("argument must not be null");
    }
    return compareTo(bjDateTime) >= 0;
  }

  /**
   * 当前时间是否在指定的时间之前？
   */
  public boolean isBefore(@NotNull BjDateTime bjDateTime) {
    if (bjDateTime == null) {
      throw new IllegalArgumentException("argument must not be null");
    }
    return compareTo(bjDateTime) < 0;
  }

  /**
   * 当前时间是否在指定的时间之前或相等？
   */
  public boolean isBeforeOrEqual(@NotNull BjDateTime bjDateTime) {
    if (bjDateTime == null) {
      throw new IllegalArgumentException("argument must not be null");
    }
    return compareTo(bjDateTime) <= 0;
  }

  /**
   * 当前时间是否与指定的时间相同：比较时间值的大小，忽略格式。精确到毫秒。
   */
  public boolean isEqual(@NotNull BjDateTime bjDateTime) {
    if (bjDateTime == null) {
      throw new IllegalArgumentException("argument must not be null");
    }
    return this.innerTime.isEqual(bjDateTime.innerTime);
  }

  /**
   * 比较两个时间的大小：比较时间值的大小，忽略格式。精确到毫秒。
   */
  public int compareTo(@NotNull BjDateTime bjDateTime) {
    if (bjDateTime == null) {
      throw new IllegalArgumentException("argument must not be null");
    }
    return this.innerTime.compareTo(bjDateTime.innerTime);
  }

  // ----------- toString --------------
  @Override
  public String toString() {
    return innerTime.toString(format);
  }
}
