package com.tehang.common.utility;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 集合工具类.
 */
public final class ThCollectionUtils {

  private ThCollectionUtils() {
    // do nothing
  }

  /**
   * 多个集合 -> list.
   */
  public static <E> List<E> unionToList(Collection<E>... arraysOfCollections) {
    if (ArrayUtils.isEmpty(arraysOfCollections)) {
      return new ArrayList<>();
    }

    return Arrays.stream(arraysOfCollections)
      // 非法数组元素
      .filter(Objects::nonNull).flatMap(Collection::stream)
      // 非法集合元素
      .filter(Objects::nonNull).collect(Collectors.toList());
  }

}