package com.tehang.common.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

}
