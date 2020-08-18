package com.tehang.common.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tehang.common.infrastructure.exceptions.ParameterException;
import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Json相关的工具
 */
public final class JsonUtils {

  /**
   * mapper
   */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)// 序列化时，只包含不为空的字段
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);// 反序列化时，忽略json中多余的字段


  private JsonUtils() {
  }

  /**
   * convert bo to json string
   */
  public static <T> String toJson(T object) {
    try {
      return object == null ? null : MAPPER.writeValueAsString(object);

    } catch (IOException ex) {
      throw new ParameterException("to Json error: " + ex.getMessage(), ex);
    }
  }

  /**
   * convert json string to bo
   */
  public static <T> T toClass(String json, Class<T> clazz) {
    try {
      return StringUtils.isBlank(json) ? null : MAPPER.readValue(json, clazz);

    } catch (IOException ex) {
      throw new ParameterException("toClass error: " + ex.getMessage(), ex);
    }
  }

  /**
   * convert json reader to bo
   */
  public static <T> T toClassFromReader(Reader reader, Class<T> clazz) {
    try {
      return reader == null ? null : MAPPER.readValue(reader, clazz);

    } catch (IOException ex) {
      throw new ParameterException("toClass error: " + ex.getMessage(), ex);
    }
  }

  /**
   * convert json string to generic type
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> T toClass(String json, TypeReference<T> valueTypeRef) {
    try {
      return StringUtils.isBlank(json) ? null : MAPPER.readValue(json, valueTypeRef);

    } catch (IOException ex) {
      throw new ParameterException("toClass error: " + ex.getMessage(), ex);
    }
  }

  /**
   * convert json string to list
   */
  public static <T> List<T> toList(String json, Class<T> clazz) {
    try {
      return StringUtils.isBlank(json) ? new ArrayList<>() :
          MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));

    } catch (IOException ex) {
      throw new ParameterException("toList error: " + ex.getMessage(), ex);
    }
  }


  /**
   * convert object to class
   */
  public static <T> T convertValue(Object object, Class<T> clazz) {
    return object == null ? null : MAPPER.convertValue(object, clazz);
  }

  /**
   * 将 Long 字段以 String 类型序列化，以规避 JavaScript 的 Long 精度不足问题。
   */
  public static class LongSerializer extends JsonSerializer<Long> {
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        gen.writeNull();
      }
      else {
        gen.writeString(value.toString());
      }
    }
  }
}
