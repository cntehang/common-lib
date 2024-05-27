package com.tehang.common.utility;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 特航封装的集合处理工具类。
 */
public final class CollectionUtils {

  private CollectionUtils() {

  }

  // ------------ 判空相关的方法 ----------

  /**
   * 判断一个集合是否为空或null。
   */
  public static boolean isEmpty(final Collection<?> coll) {
    return org.apache.commons.collections4.CollectionUtils.isEmpty(coll);
  }

  /**
   * 判断一个集合是否非空(或null)。
   */
  public static boolean isNotEmpty(final Collection<?> coll) {
    return !isEmpty(coll);
  }

  /**
   * 如果指定集合为null, 返回一个空集合，否则返回集合自身。
   */
  public static <E> Collection<E> emptyIfNull(final Collection<E> collection) {
    return org.apache.commons.collections4.CollectionUtils.emptyIfNull(collection);
  }

  // ------------ 集合元素匹配的相关方法 ----------

  /**
   * 查找集合中满足条件的第一个元素，如果未找到，或者为空集合，则返回null。
   * @param collection  查找的集合，可以为null
   * @param predicate  查找条件，不能为null
   */
  public static <E> E find(final Collection<E> collection, @NotNull final Predicate<? super E> predicate) {
    return IterableUtils.find(collection, predicate);
  }

  /**
   * 查找集合中满足条件的所有元素，返回这些元素的列表。
   * @param collection  查找的集合，可以为null，当为null时，返回空列表。
   * @param predicate  查找条件，不能为null
   */
  @NotNull
  public static <E> List<E> findAll(final Collection<E> collection, @NotNull final Predicate<? super E> predicate) {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null.");
    }
    if (collection == null) {
      return new ArrayList<>();
    }
    return collection.stream()
        .filter(predicate::evaluate)
        .collect(Collectors.toList());
  }

  /**
   * 判断集合中的元素是否全部满足指定的条件。如果集合为空或null，返回true。
   * @param iterable  查找的集合，可以为null
   * @param predicate  判断条件，不能为null
   */
  public static <E> boolean matchesAll(final Iterable<E> iterable, @NotNull final Predicate<? super E> predicate) {
    return IterableUtils.matchesAll(iterable, predicate);
  }

  /**
   * 判断集合中存在任一元素满足指定的条件。如果集合为空或null，返回false。
   * @param iterable  查找的集合，可以为null
   * @param predicate  判断条件，不能为null
   */
  public static <E> boolean matchesAny(final Iterable<E> iterable, @NotNull final Predicate<? super E> predicate) {
    return IterableUtils.matchesAny(iterable, predicate);
  }

  // ------------ 集合合并相关方法 ----------

  /** 合并多个集合为一个。*/
  @NotNull
  @SafeVarargs
  public static <E> List<E> unionAll(final Collection<E>... collections) {
    var result = new ArrayList<E>();
    if (collections != null) {
      for (var collection : collections) {
        if (collection != null) {
          result.addAll(collection);
        }
      }
    }
    return result;
  }

  /** 合并多个集合为一个。*/
  @NotNull
  public static <E> List<E> unionAll(final List<Collection<E>> collections) {
    var result = new ArrayList<E>();
    if (collections != null) {
      for (var collection : collections) {
        if (collection != null) {
          result.addAll(collection);
        }
      }
    }
    return result;
  }

  /** 根据字段值对列表中的数据去重，该字段的值不能为null。*/
  public static <E, K> List<E> distinctBy(final Collection<E> list, final Function<? super E, ? extends K> classifier) {
    if (list == null) {
      return new ArrayList<>();
    }

    var groups = list.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(classifier));

    return groups.values().stream()
        .map(item -> item.get(0))
        .collect(Collectors.toList());
  }

  // ------------ 集合转换相关方法 ----------

  /**
   * 获取一个新列表，其元素来自指定集合中的元素。当传入null时将返回一个空列表。
   * @param iterable  指定集合，可以为null
   */
  @NotNull
  public static <E> List<E> toList(final Iterable<E> iterable) {
    return IterableUtils.toList(iterable);
  }
}
