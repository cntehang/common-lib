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
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 表示北京时间，精确到毫秒，格式为yyyy-MM-dd HH:mm:ss.SSS
 */
@EqualsAndHashCode(callSuper = true)
@JsonSerialize(using = BjTime.Serializer.class)
@JsonDeserialize(using = BjTime.Deserializer.class)
public class BjTime extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  protected BjTime() {
    // 私有的无参构造函数
    super();
  }

  private BjTime(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_MS);
  }

  public BjTime(String dateString) {
    super(dateString, DATE_FORMAT_TO_MS);
  }

  // ----------- 其他函数 --------------

  /**
   * 解析yyyy-MM-dd HH:mm:ss.SSS格式的字符串为北京时间对象，对于无效的字符串将抛出IllegalArgumentException。
   */
  public static BjTime parse(String dateString) {
    return new BjTime(dateString);
  }

  public static BjTime parseOrNull(String dateString) {
    return isValid(dateString) ? parse(dateString) : null;
  }

  /**
   * 判断给定的字符串是否为有效的北京时间格式，即yyyy-MM-dd HH:mm:ss.SSS格式
   */
  public static boolean isValid(String dateString) {
    return !isBlank(dateString)
        && dateString.matches(DATE_FORMAT_TO_MS_REGEX);
  }

  /**
   * 获取北京时间当前时间，精确到毫秒
   */
  public static BjTime now() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_MS);
    return new BjTime(dateString);
  }

  public BjTime plusDays(int days) {
    return new BjTime(this.innerTime.plusDays(days));
  }

  public BjTime minusDays(int days) {
    return new BjTime(this.innerTime.minusDays(days));
  }

  public BjTime plusSeconds(int seconds) {
    return new BjTime(this.innerTime.plusSeconds(seconds));
  }

  public BjTime minusSeconds(int seconds) {
    return new BjTime(this.innerTime.minusSeconds(seconds));
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
  @javax.persistence.Converter(autoApply = true)
  public static class Converter implements AttributeConverter<BjTime, String> {

    @Override
    public String convertToDatabaseColumn(BjTime date) {
      return date == null
              ? null
              : date.toString();
    }

    @Override
    public BjTime convertToEntityAttribute(String s) {
      return isBlank(s)
              ? null
              : new BjTime(s);
    }
  }

  /**
   * 将BjTime序列化为Json。
   */
  public static class Serializer extends JsonSerializer<BjTime> {
    @Override
    public void serialize(BjTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        gen.writeNull();
      }
      else {
        gen.writeString(value.toString());
      }
    }
  }

  /**
   * 将json反序列化为BjTime。
   */
  public static class Deserializer extends JsonDeserializer<BjTime> {
    @Override
    public BjTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
      String text = p.getText();
      return isBlank(text)
              ? null
              : new BjTime(text);
    }
  }
}