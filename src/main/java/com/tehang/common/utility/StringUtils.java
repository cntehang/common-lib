package com.tehang.common.utility;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 特航封装的字符串工具类。
 */
public final class StringUtils {

  /**
   * The empty String {@code ""}.
   */
  public static final String EMPTY = "";

  /**
   * A String for a space character.
   */
  public static final String SPACE = " ";

  /**
   * 半角逗号.
   */
  public static final String COMMA = ",";

  /**
   * 半角的英文句号(点号）
   */
  public static final String DOT = ".";

  /**
   * 半角的连字符(减号）
   */
  public static final String HYPHEN = "-";

  /**
   * A String for linefeed LF ("\n").
   */
  public static final String LF = "\n";

  /**
   * A String for carriage return CR ("\r").
   */
  public static final String CR = "\r";

  /**
   * 斜杠，通常用来表示web的路径分隔符。
   */
  public static final String SLASH = "/";

  /**
   * 反斜杠，通常用来表示转义字符。
   */
  public static final String BACK_SLASH = "\\";

  private StringUtils() {

  }

  // ------------ 判空相关的方法 ----------

  /** 判断字符串是否为空白字符? */
  public static boolean isBlank(final CharSequence cs) {
    return org.apache.commons.lang3.StringUtils.isBlank(cs);
  }

  /** 判断字符串是否为非空白字符? */
  public static boolean isNotBlank(final CharSequence cs) {
    return !isBlank(cs);
  }

  /** 判断字符串是否为空字符串或null */
  public static boolean isEmpty(final CharSequence cs) {
    return org.apache.commons.lang3.StringUtils.isEmpty(cs);
  }

  /** 判断字符串是否不是空字符串或null */
  public static boolean isNotEmpty(final CharSequence cs) {
    return !isEmpty(cs);
  }

  // ------------ 判等相关的方法 ----------

  /** 判断两个字符串是否相等。两个null值视为相等。*/
  public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
    return org.apache.commons.lang3.StringUtils.equals(cs1, cs2);
  }

  /** 判断两个字符串是否相等(忽略大小写)。两个null值视为相等。*/
  public static boolean equalsIgnoreCase(final CharSequence cs1, final CharSequence cs2) {
    return org.apache.commons.lang3.StringUtils.equalsIgnoreCase(cs1, cs2);
  }

  /** 检查指定的字符串是否和给定的字符串数组中的任意一个相等，null或空值视为不等。*/
  public static boolean equalsAny(final CharSequence cs, final CharSequence... searchCharSequences) {
    if (isNotEmpty(cs) && ArrayUtils.isNotEmpty(searchCharSequences)) {
      return Arrays.stream(searchCharSequences)
          .anyMatch(searchCharSequence -> equals(cs, searchCharSequence));
    }
    return false;
  }

  /** 检查指定的字符串是否和给定的字符串集合中的任意一个相等，null或空值视为不等。*/
  public static boolean equalsAny(final CharSequence cs, final Collection<? extends CharSequence> searchCharSequences) {
    if (isNotEmpty(cs) && CollectionUtils.isNotEmpty(searchCharSequences)) {
      return searchCharSequences.stream()
          .anyMatch(searchCharSequence -> equals(cs, searchCharSequence));
    }
    return false;
  }

  /** 检查指定的字符串是否和给定的字符串数组中的任意一个相等, 比较时忽略大小写，null或空值视为不等。*/
  public static boolean equalsAnyIgnoreCase(final CharSequence cs, final CharSequence... searchCharSequences) {
    if (isNotEmpty(cs) && ArrayUtils.isNotEmpty(searchCharSequences)) {
      return Arrays.stream(searchCharSequences)
          .anyMatch(searchCharSequence -> equalsIgnoreCase(cs, searchCharSequence));
    }
    return false;
  }

  /** 检查指定的字符串是否和给定的字符串集合中的任意一个相等, 比较时忽略大小写，null或空值视为不等。*/
  public static boolean equalsAnyIgnoreCase(final CharSequence cs, final Collection<? extends CharSequence> searchCharSequences) {
    if (isNotEmpty(cs) && CollectionUtils.isNotEmpty(searchCharSequences)) {
      return searchCharSequences.stream()
          .anyMatch(searchCharSequence -> equalsIgnoreCase(cs, searchCharSequence));
    }
    return false;
  }

  // ------------ split相关的方法 ----------

  /** 将给定的字符串按照","分割。 */
  public static List<String> splitByComma(String str) {
    return splitBy(str, COMMA);
  }

  /** 将给定的字符串按照指定的字符进行分割。*/
  public static List<String> splitBy(String str, String separatorChar) {
    if (str == null) {
      return null;
    }
    String[] splitItems = org.apache.commons.lang3.StringUtils.split(str, separatorChar);
    return Lists.newArrayList(splitItems);
  }

  // ------------ join相关的方法 ----------

  /** 将指定的字符串数组使用半角逗号分隔符进行拼接。 */
  public static String joinWithComma(CharSequence... items) {
    return joinWith(COMMA, items);
  }

  /** 将指定的字符串集合使用半角逗号分隔符进行拼接。*/
  public static String joinWithComma(Iterable<? extends CharSequence> items) {
    return joinWith(COMMA, items);
  }

  /** 将指定的字符串数组使用分隔符进行拼接。分隔符不允许为null。*/
  public static String joinWith(CharSequence separator, CharSequence... items) {
    if (items == null) {
      return EMPTY;
    }
    return String.join(separator, items);
  }

  /** 将指定的字符串集合使用分隔符进行拼接。分隔符不允许为null。*/
  public static String joinWith(CharSequence separator, Iterable<? extends CharSequence> items) {
    if (items == null) {
      return EMPTY;
    }
    return String.join(separator, items);
  }

  // ------------ 获取子串相关的方法 ----------

  /** 获取指定字符串中左边的n个字符。*/
  public static String left(final String str, final int len) {
    return org.apache.commons.lang3.StringUtils.left(str, len);
  }

  // ------------ 子串匹配相关的方法 ----------

  /** 判断字符串是否包含指定的搜索字符串。对于null值返回false。*/
  public static boolean contains(final CharSequence str, final CharSequence searchStr) {
    return org.apache.commons.lang3.StringUtils.contains(str, searchStr);
  }

  /** 判断字符串是否包含指定的搜索字符串(忽略大小写)。对于null值返回false。*/
  public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
    return org.apache.commons.lang3.StringUtils.containsIgnoreCase(str, searchStr);
  }

  /** 判断字符串是否包含的前缀。startsWith(null, null) = true。*/
  public static boolean startsWith(final CharSequence str, final CharSequence prefix) {
    return org.apache.commons.lang3.StringUtils.startsWith(str, prefix);
  }

  /** 判断字符串是否包含的前缀(忽略大小写)。startsWithIgnoreCase(null, null) = true。*/
  public static boolean startsWithIgnoreCase(final CharSequence str, final CharSequence prefix) {
    return org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(str, prefix);
  }

  /** 判断字符串是否包含的后缀。endsWith(null, null) = true。*/
  public static boolean endsWith(final CharSequence str, final CharSequence suffix) {
    return org.apache.commons.lang3.StringUtils.endsWith(str, suffix);
  }

  /** 判断字符串是否包含的后缀(忽略大小写)。endsWithIgnoreCase(null, null) = true。*/
  public static boolean endsWithIgnoreCase(final CharSequence str, final CharSequence suffix) {
    return org.apache.commons.lang3.StringUtils.endsWithIgnoreCase(str, suffix);
  }

  // ------------ trim相关的方法 ----------

  /** 移除字符串首尾的空格（或控制字符）, 传入null值将返回null。*/
  public static String trim(final String str) {
    return org.apache.commons.lang3.StringUtils.trim(str);
  }

  /** 移除字符串首尾的空格（或控制字符）, 传入null值将返回String.Empty。*/
  public static String trimToEmpty(final String str) {
    return org.apache.commons.lang3.StringUtils.trimToEmpty(str);
  }

  // ------------ stringOf相关的方法 ----------

  public static String stringOf(String value) {
    return value;
  }

  public static String stringOf(long value) {
    return String.valueOf(value);
  }

  public static String stringOf(int value) {
    return String.valueOf(value);
  }

  public static String stringOf(float value) {
    return String.valueOf(value);
  }

  public static String stringOf(double value) {
    return String.valueOf(value);
  }

  public static String stringOf(boolean value) {
    return String.valueOf(value);
  }

  /** 返回一个对象的字符串表示，传入null时将返回null(同String.valueOf不同)。*/
  public static String stringOf(Object obj) {
    return (obj == null) ? null : obj.toString();
  }
}
