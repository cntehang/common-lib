package com.tehang.common.utility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * 通用的断言辅助类。
 */
public final class Assert {

  private Assert() {
  }

  /** 断言指定的表达式为true, 如果该表达式为false, 则抛出参数异常。*/
  public static void isTrue(boolean expression, String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
  }

  /** 断言指定的对象不为nul, 如果该对象为null则抛出参数异常。*/
  public static void notNull(@Nullable Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  /** 断言指定的对象为nul, 如果该对象不为null则抛出参数异常。*/
  public static void isNull(@Nullable Object object, String message) {
    if (object != null) {
      throw new IllegalArgumentException(message);
    }
  }

  /** 断言指定的字符串非空, 如果该对象为空则抛出参数异常。*/
  public static void isNotBlank(@Nullable CharSequence cs, String message) {
    if (StringUtils.isBlank(cs)) {
      throw new IllegalArgumentException(message);
    }
  }

  /** 断言指定的字符串为空（或空白字符）, 如果该对象不为空则抛出参数异常。*/
  public static void isBlank(@Nullable CharSequence cs, String message) {
    if (StringUtils.isNotBlank(cs)) {
      throw new IllegalArgumentException(message);
    }
  }

  /** 断言指定的集合非空, 如果该集合为空或null则抛出参数异常。*/
  public static void notEmpty(@Nullable Collection<?> collection, String message) {
    if (CollectionUtils.isEmpty(collection)) {
      throw new IllegalArgumentException(message);
    }
  }
}
