package com.tehang.common.utility.converter;

import com.tehang.common.utility.DateUtils;

import javax.persistence.AttributeConverter;
import java.time.LocalDateTime;

public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

  @Override
  public String convertToDatabaseColumn(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    return DateUtils.localDateTimeToString(localDateTime);
  }

  @Override
  public LocalDateTime convertToEntityAttribute(String s) {
    if (s == null) {
      return null;
    }
    return DateUtils.localDateTimeFromString(s);
  }
}
