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
 * 表示北京时间，精确到分钟，格式为yyyy-MM-dd HH:mm
 */
public final class BjTimeToMinute extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  private BjTimeToMinute() {
    // 私有的无参构造函数
    super();
  }

  private BjTimeToMinute(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_MINUTE);
  }

  public BjTimeToMinute(String dateString) {
    super(dateString, DATE_FORMAT_TO_MINUTE);
  }

  // ----------- 其他函数 --------------

  /**
   * 获取北京时间当前时间，精确到分钟
   */
  public static BjTimeToMinute now() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_MINUTE);
    return new BjTimeToMinute(dateString);
  }

  public BjTimeToMinute plusDays(int days) {
    return new BjTimeToMinute(this.innerTime.plusDays(days));
  }

  public BjTimeToMinute minusDays(int days) {
    return new BjTimeToMinute(this.innerTime.minusDays(days));
  }

  public BjTimeToMinute plusMinutes(int minutes) {
    return new BjTimeToMinute(this.innerTime.plusMinutes(minutes));
  }

  public BjTimeToMinute minusMinutes(int minutes) {
    return new BjTimeToMinute(this.innerTime.minusMinutes(minutes));
  }


  // Jpa Converter的定义
  public static class Converter implements AttributeConverter<BjTimeToMinute, String> {

    @Override
    public String convertToDatabaseColumn(BjTimeToMinute date) {
      return date == null
              ? null
              : date.toString();
    }

    @Override
    public BjTimeToMinute convertToEntityAttribute(String s) {
      return isBlank(s)
              ? null
              : new BjTimeToMinute(s);
    }
  }

  /**
   * 将BjTimeToMinute序列化为Json。
   */
  public static class Serializer extends JsonSerializer<BjTimeToMinute> {
    @Override
    public void serialize(BjTimeToMinute value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        gen.writeNull();
      }
      else {
        gen.writeString(value.toString());
      }
    }
  }

  /**
   * 将json反序列化为BjTimeToMinute。
   */
  public static class Deserializer extends JsonDeserializer<BjTimeToMinute> {
    @Override
    public BjTimeToMinute deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
      String text = p.getText();
      return isBlank(text)
              ? null
              : new BjTimeToMinute(text);
    }
  }
}
