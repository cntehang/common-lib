package com.tehang.common.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tehang.common.infrastructure.exceptions.SystemErrorException;

import java.io.IOException;
import java.util.List;

/**
 * Json相关的工具
 */
public final class JsonUtils {

  /**
   * mapper
   */
  private static ObjectMapper mapper = new ObjectMapper();

  private JsonUtils() {
  }

  /**
   * final
   * convert bo to json string
   *
   * @param object
   * @param <T>
   * @return
   * @throws IOException
   */
  public static <T> String toJson(T object) {
    try {
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      return mapper.writeValueAsString(object);
    } catch (IOException ex) {
      throw new SystemErrorException("to Json error: " + ex.getMessage(), ex);
    }
  }

  /**
   * convert json string to bo
   *
   * @param json
   * @param clazz
   * @param <T>
   * @return
   * @throws IOException
   */
  public static <T> T toClass(String json, Class<T> clazz) {
    try {
      return mapper.readValue(json, clazz);
    } catch (IOException ex) {
      throw new SystemErrorException("toClass error: " + ex.getMessage(), ex);
    }
  }

  /**
   * convert json string to generic type
   *
   * @param json
   * @param valueTypeRef
   * @param <T>
   * @return
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> T toClass(String json, TypeReference<T> valueTypeRef) {
    try {
      return mapper.readValue(json, valueTypeRef);
    } catch (IOException ex) {
      throw new SystemErrorException("toClass error: " + ex.getMessage(), ex);
    }
  }

  /**
   * convert json string to list
   *
   * @param json
   * @param clazz
   * @param <T>
   * @return
   * @throws IOException
   */
  public static <T> List<T> toList(String json, Class<T> clazz) {
    try {
      return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    } catch (IOException ex) {
      throw new SystemErrorException("toList error: " + ex.getMessage(), ex);
    }
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
