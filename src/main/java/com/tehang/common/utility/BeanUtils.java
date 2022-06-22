package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * Bean操作的一些辅助方法
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanUtils {
  /**
   * Clone一个Bean对象，浅拷贝
   */
  public static <T> T clone(Class<T> clazz, final T source) {
    if (source == null) {
      return null;
    }

    T target = org.springframework.beans.BeanUtils.instantiateClass(clazz);
    org.springframework.beans.BeanUtils.copyProperties(source, target);
    return target;
  }

  /**
   * 深拷贝一个对象
   */
  public static <T extends Serializable> T deepClone(final T input) {
    return SerializationUtils.clone(input);
  }
}
