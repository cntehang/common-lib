package com.tehang.common.utility.converter;

import com.tehang.common.utility.DateTimeUtils;
import com.tehang.common.utility.DateUtils;

import javax.persistence.AttributeConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
