package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Long类型使用的工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LongUtils {
  /**
   * Long.parseLong 方法的包装，会判断传入字符串是否为null或者blank
   *
   * @param value 传入字符串
   * @return 字符串转换后的long数值
   */
  public static Long parseDefaultNull(String value) {
    Long result = null;

    try {
      if (StringUtils.isNotBlank(value)) {
        result = Long.parseLong(value);
      }
    } catch (NumberFormatException numberFormatException) {
      log.error("Can not parse string: {} to long with format exception", value);
    }

    return result;
  }

  public static String toString(Long value) {
    if (value == null) {
      return null;
    }
    else {
      return value.toString();
    }
  }

  /**
   * 比较两个Long类型的值是否相等，为null则不等
   */
  public static boolean valueEquals(Long num1, Long num2) {
    return num1 != null
        && num2 != null
        && num1.compareTo(num2) == 0;
  }
}
