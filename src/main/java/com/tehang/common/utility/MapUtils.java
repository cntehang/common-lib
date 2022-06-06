package com.tehang.common.utility;

import com.tehang.common.infrastructure.exceptions.ParameterException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapUtils {

  private static final String ERROR_MSG = "convert obj to map occurred exception:";

  /**
   * 将对象转换为Map
   */
  public static Map<String, Object> convertObjToMap(Object obj) {
    if (obj == null) {
      log.warn(ERROR_MSG.concat(" object is null"));
      throw new ParameterException(ERROR_MSG.concat(" object is null"));
    }

    Map<String, Object> map = new ConcurrentHashMap<>();
    Field[] fields = obj.getClass().getDeclaredFields();

    for (Field value : fields) {
      try {
        Field field = obj.getClass().getDeclaredField(value.getName());
        field.setAccessible(true);
        Object object = field.get(obj);
        if (object != null) {
          map.put(value.getName(), object);
        }
      }
      catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
        log.warn(ERROR_MSG.concat(ex.getMessage()), ex);
        throw new ParameterException(ERROR_MSG.concat(ex.getMessage()));
      }
    }

    return map;
  }
}
