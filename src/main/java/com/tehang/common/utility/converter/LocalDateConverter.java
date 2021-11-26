package com.tehang.common.utility.converter;

import com.tehang.common.utility.DateTimeUtils;

import javax.persistence.AttributeConverter;
import java.time.LocalDate;

public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

  @Override
  public String convertToDatabaseColumn(LocalDate localDate) {
    if (localDate == null) {
      return null;
    }
    return DateTimeUtils.localDateToString(localDate);
  }

  @Override
  public LocalDate convertToEntityAttribute(String s) {
    if (s == null) {
      return null;
    }
    return DateTimeUtils.localDateFromString(s);
  }
}
