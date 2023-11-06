package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 提供stream操作的一些辅助方法。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtils {

  /**
   * 返回指定集合的stream。传入null时将传返回一个emptyStream。
   * @param collection 指定的集合
   */
  @NotNull
  public static <E> Stream<E> stream(Collection<E> collection) {
    return collection == null ? Stream.empty() : collection.stream();
  }

  /**
   * 返回指定数组的stream。传入null时将传返回一个emptyStream。
   * @param values  指定的数组对象
   */
  @NotNull
  @SafeVarargs
  public static<T> Stream<T> stream(T... values) {
    return values == null ? Stream.empty() : Arrays.stream(values);
  }

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
