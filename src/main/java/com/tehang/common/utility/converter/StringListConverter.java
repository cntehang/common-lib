package com.tehang.common.utility.converter;

import com.tehang.common.utility.ThStringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

/**
 * 从容应对数据库 null 值，以及传入时候的 null 参数 字符串列表和数据库字段的转换类, 数据库中以逗号分隔.
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

  /**
   * 字符串列表，转换为数据库字段。规则：用逗号分隔.
   *
   * @param attribute 字符串列表
   * @return 转换后的字符串。逗号分隔
   */
  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    String dbData = StringUtils.EMPTY;
    if (CollectionUtils.isNotEmpty(attribute)) {
      dbData = String.join(ThStringUtils.COMMA_STRING, attribute);
    }
    return dbData;
  }

  /**
   * 数据库字段，转换为字符串列表。规则：按逗号拆分.
   *
   * @param dbData 数据库字段
   * @return 转换后的列表
   */
  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    List<String> attribute = new ArrayList<>();
    if (StringUtils.isNotBlank(dbData)) {
      attribute = ThStringUtils.splitByComma(dbData);
    }
    return attribute;
  }

}
