package com.tehang.common.utility.converter;

import com.tehang.common.utility.DateTimeUtils;

import javax.persistence.AttributeConverter;
import java.time.Instant;

public class InstantConverter implements AttributeConverter<Instant, String> {

  @Override
  public String convertToDatabaseColumn(Instant instant) {
    if (instant == null) {
      return null;
    }
    return DateTimeUtils.instantToString(instant);
  }

  @Override
  public Instant convertToEntityAttribute(String s) {
    if (s == null) {
      return null;
    }
    return DateTimeUtils.instantFromString(s);
  }
}
