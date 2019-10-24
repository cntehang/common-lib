package com.tehang.common.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tehang.common.infrastructure.exceptions.SystemErrorException;

import java.io.IOException;
import java.util.List;

/**
 * xml 的序列化、反序列化工具
 */
public final class XmlUtils {

  /**
   * mapper
   */
  private static XmlMapper mapper = new XmlMapper();

  private XmlUtils() {
  }


  /**
   * convert class to xml string
   *
   * @param object
   * @param <T>
   * @return
   * @throws IOException
   */
  public static <T> String toXml(T object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (IOException ex) {
      throw new SystemErrorException("to xml error: " + ex.getMessage(), ex);
    }
  }

  /**
   * convert xml string to class
   *
   * @param xmlStr
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> T toClass(String xmlStr, Class<T> clazz) {
    try {
      return mapper.readValue(xmlStr, clazz);
    } catch (IOException ex) {
      throw new SystemErrorException("toClass error: " + ex.getMessage(), ex);
    }
  }

  /**
   * convert xml string to generic type
   *
   * @param xmlStr
   * @param valueTypeRef
   * @param <T>
   * @return
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> T toClass(String xmlStr, TypeReference<T> valueTypeRef) {
    try {
      return mapper.readValue(xmlStr, valueTypeRef);
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
