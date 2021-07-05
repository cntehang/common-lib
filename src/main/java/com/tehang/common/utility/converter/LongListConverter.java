package com.tehang.common.utility.converter;

import com.tehang.common.utility.ThStringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 从容应对数据库 null 值，以及传入时候的 null 参数 Long列表和数据库字段的转换类.
 */
@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {

  /**
   * 字符串列表，转换为数据库字段。规则：用逗号分隔.
   *
   * @param attribute 字符串列表
   * @return 转换后的字符串。逗号分隔
   */
  @Override
  public String convertToDatabaseColumn(List<Long> attribute) {
    String dbData = StringUtils.EMPTY;
    if (CollectionUtils.isNotEmpty(attribute)) {
      dbData = attribute.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(ThStringUtils.COMMA_STRING));
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
  public List<Long> convertToEntityAttribute(String dbData) {
    List<Long> attribute = new ArrayList<>();
    if (StringUtils.isNotBlank(dbData)) {
      attribute = ThStringUtils.splitByComma(dbData).stream().filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());
    }
    return attribute;
  }
}
