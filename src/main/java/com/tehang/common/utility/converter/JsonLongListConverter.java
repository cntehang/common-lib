package com.tehang.common.utility.converter;

import com.tehang.common.utility.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

/**
 * 从容应对数据库 null 值，以及传入时候的 null 参数 字符串 - JSON 转换类.
 */
@Converter
public class JsonLongListConverter implements AttributeConverter<List<Long>, String> {


  /**
   * 字符串列表，转换为json字符串.
   *
   * @param attribute 字符串列表
   * @return 转换后的字符串。json字符串
   */
  @Override
  public String convertToDatabaseColumn(List<Long> attribute) {
    String dbData = StringUtils.EMPTY;
    if (CollectionUtils.isNotEmpty(attribute)) {
      dbData = JsonUtils.toJson(attribute);
    }
    return dbData;
  }

  /**
   * json字符串，转换为字符串列表.
   *
   * @param dbData 数据库字段，json字符串
   * @return 转换后的列表
   */
  @Override
  public List<Long> convertToEntityAttribute(String dbData) {
    List<Long> attribute = new ArrayList<>();
    if (StringUtils.isNotBlank(dbData)) {
      attribute = JsonUtils.toList(dbData, Long.class);
    }
    return attribute;
  }

}
