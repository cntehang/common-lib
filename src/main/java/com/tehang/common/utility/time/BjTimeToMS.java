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
 * 表示北京时间，精确到毫秒，格式为yyyy-MM-dd HH:mm:ss.SSS
 */
public final class BjTimeToMS extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  private BjTimeToMS() {
    // 私有的无参构造函数
    super();
  }

  private BjTimeToMS(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_MS);
  }

  public BjTimeToMS(String dateString) {
    super(dateString, DATE_FORMAT_TO_MS);
  }

  // ----------- 其他函数 --------------

  /**
   * 获取北京时间当前时间，精确到毫秒
   */
  public static BjTimeToMS now() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_MS);
    return new BjTimeToMS(dateString);
  }

  public BjTimeToMS plusDays(int days) {
    return new BjTimeToMS(this.innerTime.plusDays(days));
  }

  public BjTimeToMS minusDays(int days) {
    return new BjTimeToMS(this.innerTime.minusDays(days));
  }

  public BjTimeToMS plusSeconds(int seconds) {
    return new BjTimeToMS(this.innerTime.plusSeconds(seconds));
  }

  public BjTimeToMS minusSeconds(int seconds) {
    return new BjTimeToMS(this.innerTime.minusSeconds(seconds));
  }


  // Jpa Converter的定义
  public static class Converter implements AttributeConverter<BjTimeToMS, String> {

    @Override
    public String convertToDatabaseColumn(BjTimeToMS date) {
      return date == null
              ? null
              : date.toString();
    }

    @Override
    public BjTimeToMS convertToEntityAttribute(String s) {
      return isBlank(s)
              ? null
              : new BjTimeToMS(s);
    }
  }

  /**
   * 将BjTimeToMS序列化为Json。
   */
  public static class Serializer extends JsonSerializer<BjTimeToMS> {
    @Override
    public void serialize(BjTimeToMS value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        gen.writeNull();
      }
      else {
        gen.writeString(value.toString());
      }
    }
  }

  /**
   * 将json反序列化为BjTimeToMS。
   */
  public static class Deserializer extends JsonDeserializer<BjTimeToMS> {
    @Override
    public BjTimeToMS deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
      String text = p.getText();
      return isBlank(text)
              ? null
              : new BjTimeToMS(text);
    }
  }
}
