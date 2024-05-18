package com.tehang.common.utility;

import com.tehang.common.infrastructure.exceptions.ParameterException;
import com.tehang.common.utility.time.BjDate;
import com.tehang.common.utility.time.BjDateTime;
import com.tehang.common.utility.time.BjTime;
import com.tehang.common.utility.time.BjTimeToMinute;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Years;

import java.time.Year;

import static com.tehang.common.utility.DateUtils.ISO_PATTERN;
import static com.tehang.common.utility.time.BjDateTime.ZONE_SHANGHAI;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * BjDate工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BjDateUtils {

  /**
   * 获取两个日期之间的间隔天数
   */
  public static int daysBetween(BjDate startDate, BjDate endDate) {
    if (startDate == null) {
      throw new ParameterException("开始日期不能为空");
    }

    if (endDate == null) {
      throw new ParameterException("结束日期不能为空");
    }

    return Days.daysBetween(startDate.getInnerTime().withTimeAtStartOfDay(), endDate.getInnerTime().withTimeAtStartOfDay()).getDays();
  }

  /**
   * 获取两个时间之间的间隔分钟
   */
  public static int minutesBetween(BjTimeToMinute startTime, BjTimeToMinute endTime) {
    if (startTime == null) {
      throw new ParameterException("开始时间不能为空");
    }

    if (endTime == null) {
      throw new ParameterException("结束时间不能为空");
    }

    return Minutes.minutesBetween(startTime.getInnerTime(), endTime.getInnerTime()).getMinutes();
  }

  /**
   * 通过出生日期和当前日期计算年龄（周岁）
   */
  public static int calculateAge(BjDate birthday, BjDate currentDate) {
    if (birthday == null) {
      throw new ParameterException("出生日期不能为空");
    }
    if (currentDate == null) {
      throw new ParameterException("当前日期不能为空");
    }
    if (currentDate.isBeforeOrEqual(birthday)) {
      return 0;
    }

    DateTime birthdayInnerTime = birthday.getInnerTime();
    DateTime currentDateInnerTime = currentDate.getInnerTime();

    int yearsBetweenTwoDate = Years.yearsBetween(birthdayInnerTime, currentDateInnerTime).getYears();

    if (Year.isLeap(birthdayInnerTime.getYear()) && birthdayInnerTime.getMonthOfYear() == 2 && birthdayInnerTime.getDayOfMonth() == 29 &&
        !Year.isLeap(currentDateInnerTime.getYear()) && currentDateInnerTime.getMonthOfYear() == 2 && currentDateInnerTime.getDayOfMonth() == 28) {
      // 公民出生日期为闰年2月29日的，在计算周岁时，遇到非闰年以3月1日为满周岁；如某公民出生日期为1992年2月29日，该公民满18周岁的日期为2010年3月1日。
      // yearsBetweenTwoDate 计算闰年和非闰年之间的年数时，会认为 1992-02-29 与 1993-02-28 之间有一整年（把 02-29 与非闰年的 02-28 视作等价），因此这里特殊处理减掉一年
      return yearsBetweenTwoDate - 1;
    }
    else {
      return yearsBetweenTwoDate;
    }
  }

  /**
   * 解析utc格式(yyyy-MM-dd'T'HH:mm:ss.SSS'Z')的字符串转为北京时间对象。
   */
  public static BjTime parseBjTimeInUtc(String utcTimeString) {
    if (isBlank(utcTimeString)) {
      return null;
    }

    String utcNormalString = StringUtils.replace(utcTimeString, StringUtils.SPACE, "T");
    DateTime utcTime = new DateTime(utcNormalString, DateTimeZone.UTC);
    DateTime bjTime = utcTime.toDateTime(DateTimeZone.forID(ZONE_SHANGHAI));

    return BjTime.parse(bjTime.toString(BjDateTime.DATE_FORMAT_TO_MS));
  }

  /**
   * 将北京时间对象转换为utc格式(yyyy-MM-dd'T'HH:mm:ss.SSS'Z')的字符串。
   */
  public static String toUtcTimeString(BjDateTime bjDateTime) {
    if (bjDateTime == null) {
      return null;
    }
    return bjDateTime.getInnerTime().toDateTime(DateTimeZone.UTC).toString(ISO_PATTERN);
  }
}
