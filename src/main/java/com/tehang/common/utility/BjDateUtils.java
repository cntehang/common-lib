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
}
