package com.tehang.common.utility;

import com.tehang.common.infrastructure.exceptions.ParameterException;
import com.tehang.common.utility.time.BjDate;
import com.tehang.common.utility.time.BjTimeToMinute;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Days;
import org.joda.time.Minutes;

/**
 * BjDate工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BjDateUtils {

  /**
   * 获取两个日期之间的间隔天数
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @return 间隔天数
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
   * @param startTime 开始时间
   * @param endTime 结束时间
   * @return 间隔分钟
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
}
