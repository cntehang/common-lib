package com.tehang.common.utility.converter;

import com.tehang.common.utility.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;

/**
 * 从容应对数据库 null 值，以及传入时候的 null 参数
 */
public class JsonToObjectConverter<T> implements AttributeConverter<T, String> {

  @Override
  public String convertToDatabaseColumn(T attribute) {
    return attribute == null ? null : JsonUtils.toJson(attribute);
  }

  @Override
  public T convertToEntityAttribute(String dbData) {
    if (StringUtils.isNotBlank(dbData)) {
      Class<T> clazz = getActualTypeArgument();
      return JsonUtils.toClass(dbData, clazz);

    } else {
      return null;
    }
  }

  private Class<T> getActualTypeArgument() {
    return (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }
}
