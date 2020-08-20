package com.tehang.common.utility;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字符串工具类
 */
public final class ThStringUtils {


  public static final String SPLITTER = "/";

  /**
   * 半角逗号
   */
  public static final String COMMA_STRING = ",";

  /**
   * 全角逗号
   */
  public static final String ANGLE_COMMA = "，";

  /**
   * 中文分号
   */
  public static final String ANGLE_SEMICOLON = "；";

  /**
   * 点
   */
  public static final String POINT_STRING = ".";

  /**
   * 横杠
   */
  public static final String LINE = "-";

  private ThStringUtils() {
  }

  /**
   * 检查指定的字符串是否和给定的字符组数组中的任意一个相等
   *
   * @param cs                  要检查的字符串
   * @param searchCharSequences 给定字符串数组
   * @return 是否与任意相等
   */
  public static boolean equalsAny(final CharSequence cs, final CharSequence... searchCharSequences) {
    boolean result = false;
    if (StringUtils.isNotEmpty(cs) && ArrayUtils.isNotEmpty(searchCharSequences)) {
      result = Arrays.stream(searchCharSequences)
          .anyMatch(searchCharSequence -> StringUtils.equals(cs, searchCharSequence));
    }
    return result;
  }

  /**
   * 检查指定的字符串是否和给定的字符组数组中的任意一个相等, 比较时忽略大小写
   *
   * @param cs                  要检查的字符串
   * @param searchCharSequences 给定字符串数组
   * @return 是否与任意相等
   */
  public static boolean equalsAnyIgnoreCase(final CharSequence cs, final CharSequence... searchCharSequences) {
    boolean result = false;
    if (StringUtils.isNotEmpty(cs) && ArrayUtils.isNotEmpty(searchCharSequences)) {
      result = Arrays.stream(searchCharSequences)
          .anyMatch(searchCharSequence -> StringUtils.equalsIgnoreCase(cs, searchCharSequence));
    }
    return result;
  }

  /**
   * 把字符串按照","分割
   */
  public static List<String> splitByComma(String item) {
    return splitBy(item, COMMA_STRING);
  }

  /**
   * 把字符串按照"."分割
   */
  public static List<String> splitByPoint(String item) {
    return splitBy(item, POINT_STRING);
  }

  /**
   * 把字符串按照"/"分割
   */
  public static List<String> splitBySlash(String item) {
    return splitBy(item, SPLITTER);
  }

  /**
   * 把字符串按照"-"分割
   */
  public static List<String> splitByLine(String item) {
    return splitBy(item, LINE);
  }

  /**
   * 把字符串按照指定的字符进行分割
   */
  public static List<String> splitBy(String item, String separatorChar) {
    String[] splitItems = StringUtils.split(trimToEmpty(item), separatorChar);
    return Lists.newArrayList(splitItems);
  }

  /**
   * 把字符串按照指定的字符进行分割
   */
  public static List<String> splitBy(String item, char separatorChar) {
    String[] splitItems = StringUtils.split(trimToEmpty(item), separatorChar);
    return Lists.newArrayList(splitItems);
  }

  /**
   * 把字符串按照指定的字符进行分割, 取第一个
   */
  public static String splitFirstBy(String item, char separatorChar) {
    List<String> splitItems = splitBy(item, separatorChar);
    if (CollectionUtils.isNotEmpty(splitItems)) {
      return splitItems.get(0);
    }
    return null;
  }

  /**
   * 把字符串按照指定的字符进行分割, 取最后一个
   */
  public static String splitLastBy(String item, char separatorChar) {
    List<String> splitItems = splitBy(item, separatorChar);
    if (CollectionUtils.isNotEmpty(splitItems)) {
      return splitItems.get(splitItems.size() - 1);
    }
    return null;
  }

  /**
   * 把List转为字符串，逗号隔开
   *
   * @param items 要分割的元素
   * @return 转换后的字符串
   */
  public static String joinWithComma(Collection<String> items) {
    return joinWith(Lists.newArrayList(CollectionUtils.emptyIfNull(items)), COMMA_STRING);
  }

  /**
   * 把数组转为字符串，横杠隔开
   *
   * @param items 要分割的元素
   * @return 转换后的字符串
   */
  public static String joinWithLine(String... items) {
    return joinWith(Lists.newArrayList(ArrayUtils.nullToEmpty(items)), LINE);

  }

  /**
   * 把数组转为字符串，横杠隔开
   *
   * @param items 要分割的元素
   * @return 转换后的字符串
   */
  public static String joinWithLine(List<String> items) {
    return joinWith(items, LINE);

  }

  /**
   * 把List转为字符串，斜杠隔开
   *
   * @param items 要分割的元素
   * @return 转换后的字符串
   */
  public static String joinWithSlash(Collection<String> items) {
    return joinWith(items, SPLITTER);
  }

  /**
   * 把List转为字符串，斜杠隔开
   *
   * @param items 要分割的元素
   * @return 转换后的字符串
   */
  public static String joinWith(Collection<String> items, String separatorChar) {
    return CollectionUtils.emptyIfNull(items).stream()
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.joining(separatorChar));
  }

  /**
   * 取得字符串的值，当字符串为空时，返回指定的默认值
   */
  public static String valueOrDefault(String value, String defaultValue) {
    return StringUtils.isNotBlank(value) ? value : defaultValue;
  }

  /**
   * 如果对象非空，返回对象的toString()表示，并trim()；如果对象为null,则返回StringUtils.EMPTY
   */
  public static String trimToEmpty(final Object obj) {
    return obj == null ? StringUtils.EMPTY : obj.toString().trim();
  }

}