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
import com.tehang.common.utility.BjDateUtils;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 表示北京时间，精确到天，格式为yyyy-MM-dd。
 */
@EqualsAndHashCode(callSuper = true)
@JsonSerialize(using = BjDate.Serializer.class)
@JsonDeserialize(using = BjDate.Deserializer.class)
public class BjDate extends BjDateTime implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  // ----------- 构造函数 --------------
  protected BjDate() {
    // 私有的无参构造函数
    super();
  }

  protected BjDate(DateTime innerTime) {
    super(innerTime, DATE_FORMAT_TO_DAY);
  }

  public BjDate(String dateString) {
    super(dateString, DATE_FORMAT_TO_DAY);
  }

  public BjDate(int year, int month, int day) {
    this(new DateTime(year, month, day, 0, 0, 0, 0, DateTimeZone.forID(ZONE_SHANGHAI)));
  }

  // ----------- 其他函数 --------------

  /**
   * 解析yyyy-MM-dd格式的字符串为北京时间对象，对于无效的字符串将抛出IllegalArgumentException。
   */
  public static BjDate parse(String dateString) {
    return new BjDate(dateString);
  }

  public static BjDate parseOrNull(String dateString) {
    return isValid(dateString) ? parse(dateString) : null;
  }

  public static BjDate parse(BjDateTime bjDateTime) {
    return bjDateTime == null
        ? null
        : new BjDate(bjDateTime.toString(DATE_FORMAT_TO_DAY));
  }

  /**
   * 判断给定的字符串是否为有效的北京日期格式，即yyyy-MM-dd格式
   */
  public static boolean isValid(String dateString) {
    return !isBlank(dateString)
        && dateString.matches(DATE_FORMAT_TO_DAY_REGEX);
  }

  /**
   * 获取北京时间当前的日期，精确到天
   */
  public static BjDate today() {
    DateTime cstNow = new DateTime(DateTimeZone.forID(ZONE_SHANGHAI));
    String dateString = cstNow.toString(DATE_FORMAT_TO_DAY);
    return new BjDate(dateString);
  }

  /** 计算两个日期之间的天数，startDate大于endDate时返回负数。*/
  public static int daysBetween(BjDate startDate, BjDate endDate) {
    return BjDateUtils.daysBetween(startDate, endDate);
  }

  public BjDate plusDays(int days) {
    return new BjDate(this.innerTime.plusDays(days));
  }

  public BjDate minusDays(int days) {
    return new BjDate(this.innerTime.minusDays(days));
  }

  public BjDate plusMonths(int months) {
    return new BjDate(this.innerTime.plusMonths(months));
  }

  public BjDate minusMonths(int months) {
    return new BjDate(this.innerTime.minusMonths(months));
  }

  public BjDate plusYears(int years) {
    return new BjDate(this.innerTime.plusYears(years));
  }

  public BjDate minusYears(int years) {
    return new BjDate(this.innerTime.minusYears(years));
  }

  /**
   * 是否晚于今天?
   */
  @JsonIgnore
  public boolean isAfterToday() {
    return isAfter(today());
  }

  /**
   * 是否早于今天？
   */
  @JsonIgnore
  public boolean isBeforeToday() {
    return isBefore(today());
  }

  /**
   * 是否正好是今天？
   */
  @JsonIgnore
  public boolean isToday() {
    return isEqual(today());
  }

  // ----------- 相关的转换类 --------------

  // Jpa Converter的定义
  @javax.persistence.Converter(autoApply = true)
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
