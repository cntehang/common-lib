package com.tehang.common.utility.time;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 表示北京时间，精确到分钟，格式为yyyy-MM-dd HH:mm
 */
@JsonSerialize(using = BjTimeToMinute.Serializer.class)
@JsonDeserialize(using = BjTimeToMinute.Deserializer.class)
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
   * 解析yyyy-MM-dd HH:mm格式的字符串为北京时间对象，对于无效的字符串将抛出IllegalArgumentException。
   */
  public static BjTimeToMinute parse(String dateString) {
    return new BjTimeToMinute(dateString);
  }

  public static BjTimeToMinute parseOrNull(String dateString) {
    return isValid(dateString) ? parse(dateString) : null;
  }

  /**
   * 判断给定的字符串是否为有效的北京时间格式，即yyyy-MM-dd HH:mm格式
   */
  public static boolean isValid(String dateString) {
    return !isBlank(dateString)
        && dateString.matches(DATE_FORMAT_TO_MINUTE_REGEX);
  }

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

  /**
   * 是否晚于当前时间?
   */
  @JsonIgnore
  public boolean isAfterNow() {
    return isAfter(now());
  }

  /**
   * 是否早于当前时间？
   */
  @JsonIgnore
  public boolean isBeforeNow() {
    return isBefore(now());
  }

  // ----------- 相关的转换类 --------------

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
