package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 提供stream操作的一些辅助方法。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtils {

  /**
   * 根据对象相关的字段值，去除stream中重复的对象。
   * 示例：
   * List<Person> distinctPersons = persons.stream()
   *       .filter(distinctByKeys(Person::firstName, Person::lastName))
   *       .collect(Collectors.toList());
   */
  @SafeVarargs
  public static <T> Predicate<T> distinctByKeys(final Function<? super T, ?>... keyExtractors) {
    Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

    return t -> {
      List<?> keys = Arrays.stream(keyExtractors)
          .map(ke -> ke.apply(t))
          .collect(Collectors.toList());

      return seen.putIfAbsent(keys, Boolean.TRUE) == null;
    };
  }

  /**
   * 根据字段值对列表中的数据去重。
   */
  @SafeVarargs
  public static <T> List<T> distinctList(final List<T> list, final Function<? super T, ?>... keyExtractors) {
    return list
        .stream()
        .filter(distinctByKeys(keyExtractors))
        .collect(Collectors.toList());
  }
}
