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
 * 表示北京时间，精确到秒，格式为yyyy-MM-dd HH:mm:ss
 */
public final class BjTimeToSecond extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  private BjTimeToSecond() {
    // 私有的无参构造函数
    super();
  }

  private BjTimeToSecond(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_SECOND);
  }

  public BjTimeToSecond(String dateString) {
    super(dateString, DATE_FORMAT_TO_SECOND);
  }

  // ----------- 其他函数 --------------

  /**
   * 获取北京时间当前时间，精确到秒
   */
  public static BjTimeToSecond now() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_SECOND);
    return new BjTimeToSecond(dateString);
  }

  public BjTimeToSecond plusDays(int days) {
    return new BjTimeToSecond(this.innerTime.plusDays(days));
  }

  public BjTimeToSecond minusDays(int days) {
    return new BjTimeToSecond(this.innerTime.minusDays(days));
  }

  public BjTimeToSecond plusSeconds(int seconds) {
    return new BjTimeToSecond(this.innerTime.plusSeconds(seconds));
  }

  public BjTimeToSecond minusSeconds(int seconds) {
    return new BjTimeToSecond(this.innerTime.minusSeconds(seconds));
  }


  // Jpa Converter的定义
  public static class Converter implements AttributeConverter<BjTimeToSecond, String> {

    @Override
    public String convertToDatabaseColumn(BjTimeToSecond date) {
      return date == null
              ? null
              : date.toString();
    }

    @Override
    public BjTimeToSecond convertToEntityAttribute(String s) {
      return isBlank(s)
              ? null
              : new BjTimeToSecond(s);
    }
  }

  /**
   * 将BjTimeToSecond序列化为Json。
   */
  public static class Serializer extends JsonSerializer<BjTimeToSecond> {
    @Override
    public void serialize(BjTimeToSecond value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        gen.writeNull();
      }
      else {
        gen.writeString(value.toString());
      }
    }
  }

  /**
   * 将json反序列化为BjTimeToSecond。
   */
  public static class Deserializer extends JsonDeserializer<BjTimeToSecond> {
    @Override
    public BjTimeToSecond deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
      String text = p.getText();
      return isBlank(text)
              ? null
              : new BjTimeToSecond(text);
    }
  }
}
