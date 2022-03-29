package com.tehang.common.utility.time;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 表示北京时间，精确到天，格式为yyyy-MM-dd。
 */
public final class BjDate extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  private BjDate() {
    // 私有的无参构造函数
    super();
  }

  private BjDate(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_DAY);
  }

  public BjDate(String dateString) {
    super(dateString, DATE_FORMAT_TO_DAY);
  }

  // ----------- 其他函数 --------------

  /**
   * 获取北京时间当前的日期，精确到天
   */
  public static BjDate today() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_DAY);
    return new BjDate(dateString);
  }

  public BjDate plusDays(int days) {
    return new BjDate(this.innerTime.plusDays(days));
  }

  public BjDate minusDays(int days) {
    return new BjDate(this.innerTime.minusDays(days));
  }


  // Jpa Converter的定义
  public static class Converter implements AttributeConverter<BjDate, String> {

    @Override
    public String convertToDatabaseColumn(BjDate date) {
      return date == null
              ? null
              : date.toString();
    }

    @Override
    public BjDate convertToEntityAttribute(String s) {
      return isBlank(s)
              ? null
              : new BjDate(s);
    }
  }

  /**
   * 将BjDate序列化为Json。
   */
  public static class Serializer extends JsonSerializer<BjDate> {
    @Override
    public void serialize(BjDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        gen.writeNull();
      }
      else {
        gen.writeString(value.toString());
      }
    }
  }

  /**
   * 将json反序列化为BjDate。
   */
  public static class Deserializer extends JsonDeserializer<BjDate> {
    @Override
    public BjDate deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
      String text = p.getText();
      return isBlank(text)
              ? null
              : new BjDate(text);
    }
  }
}