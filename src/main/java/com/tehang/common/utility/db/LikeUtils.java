package com.tehang.common.utility.db;

import org.apache.commons.lang3.StringUtils;

/**
 * 构建like
 */
public final class LikeUtils {


  private static final String START_LIKE = "%%%s";

  private static final String END_LIKE = "%s%%";

  private static final String WHOLE_LIKE = "%%%s%%";

  private static final String EVERY_THING = "%%";


  private LikeUtils() {
    // do nothing
  }

  /**
   * 左相似
   */
  public static String getStartLike(String value) {
    String result;
    if (StringUtils.isBlank(value)) {
      result = EVERY_THING;

    } else {
      result = String.format(START_LIKE, value.trim());
    }
    return result;
  }

  /**
   * 右相似
   */
  public static String getEndLike(String value) {
    String result;
    if (StringUtils.isBlank(value)) {
      result = EVERY_THING;

    } else {
      result = String.format(END_LIKE, value.trim());
    }
    return result;
  }

  /**
   * 左右相似
   */
  public static String getWholeLike(String value) {
    String result;
    if (StringUtils.isBlank(value)) {
      result = EVERY_THING;

    } else {
      result = String.format(WHOLE_LIKE, value.trim());
    }
    return result;
  }
}
