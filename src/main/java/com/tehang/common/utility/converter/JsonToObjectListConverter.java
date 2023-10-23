package com.tehang.common.utility.converter;

import com.tehang.common.utility.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 转换List对象为json对象。
 */
public class JsonToObjectListConverter<T> implements AttributeConverter<List<T>, String> {

  @Override
  public String convertToDatabaseColumn(List<T> attribute) {
    return attribute == null ? null : JsonUtils.toJson(attribute);
  }

  @Override
  public List<T> convertToEntityAttribute(String dbData) {
    if (StringUtils.isNotBlank(dbData)) {
      return JsonUtils.toList(dbData, getActualTypeArgument());
    }
    else {
      return null;
    }
  }

  private Class<T> getActualTypeArgument() {
    return (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }
}
