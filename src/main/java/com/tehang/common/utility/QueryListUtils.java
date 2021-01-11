package com.tehang.common.utility;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 列表查询工具类
 */
public final class QueryListUtils {
  private QueryListUtils() {
    // do nothing
  }

  /**
   * 计算查询结果匹配度
   * 多个字段依次比对，取最大值
   */
  public static double calculateSearchResultsMatchesDegree(String searchParam, String... fieldsResults) {
    return Arrays.stream(ArrayUtils.nullToEmpty(fieldsResults))
        .map(item -> calculateSearchResultMatchesDegree(searchParam, item))
        // 多个字段进行计算后，取最大的匹配结果
        .max(Double::compare)
        .orElse((double) 0);
  }

  /**
   * 计算查询结果匹配度，规则：
   * 1. 按名称进行匹配
   * 2. 全匹配优先级 > 前匹配 > 非前匹配的糊糊匹配
   * 3. 字数占全称比例高的优先
   */
  public static double calculateSearchResultMatchesDegree(String searchParam, String fieldResult) {
    // 没匹配上，优先级最低
    if (StringUtils.isEmpty(fieldResult) || !fieldResult.contains(searchParam)) {
      return 0;
    }

    boolean preMatched = StringUtils.startsWithIgnoreCase(fieldResult, searchParam);

    // 前匹配时，指定一个基数100，以保证前匹配的排序值总是大于非前匹配的排序值
    double preMatchedBase = preMatched ? 100 : 0;

    // 匹配的字数百分比: 0~1
    double matchedPercent = (double) searchParam.length() / (double) fieldResult.length();

    // 匹配基数 + 匹配字数百分比， 作为最终排序的依据
    return preMatchedBase + matchedPercent;
  }

}