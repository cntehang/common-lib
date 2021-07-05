package com.tehang.common.utility.db;

import org.apache.commons.lang3.StringUtils;

/**
 * 构建 sort by direction.
 */
public final class SortUtils {

  private static final String ASC = "asc";

  private static final String DESC = "desc";

  private SortUtils() {
    // do nothing
  }

  /**
   * 构建 SQL Direction 时，若传入了 ASC 则使用升序，否则使用默认的降序.
   */
  public static String formatDirection(String direction) {
    String result = DESC;

    if (StringUtils.isNotBlank(direction) && StringUtils.equalsIgnoreCase(direction, ASC)) {
      result = ASC;
    }
    return result;
  }
}
