package com.tehang.common.utility.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeConverter;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 表示百分比，精确到两位小数, 比如15.88%。一般用于金额计算。
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode
@JsonSerialize(using = Percent.Serializer.class)
@JsonDeserialize(using = Percent.Deserializer.class)
public class Percent implements Serializable, Comparable<Percent> {

  private static final long serialVersionUID = -5962799069942105993L;

  private static final DecimalFormat PERCENT_DEFAULT_FORMAT = new DecimalFormat("#.##");

  private static final DecimalFormat NONE_DECIMAL_FORMAT = new DecimalFormat("0");
  private static final DecimalFormat ONE_DECIMAL_FORMAT = new DecimalFormat("0.0");
  private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");
  private static final int SCALE = 2;

  public static final Percent ZERO = new Percent();

  // ----------- 内部字段 --------------
  /**
   * 内部持有的百分比的数值，精确到2位小数，比如15.88%这里保存的是15.88
   */
  private final BigDecimal amount;

  // ----------- 构造函数 --------------

  protected Percent() {
    // 私有的默认构造函数，默认为0
    this(BigDecimal.ZERO);
  }

  /**
   * 根据指定的BigDecimal初始化一个百分比对象，比如：传入15.88得到一个15.88%的对分比对象。
   */
  public Percent(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("amount should not be null");
    }
    // 默认2位小数，四舍五入
    this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
  }

  /**
   * 根据字符串解析得到一个百分比对象，比如：传入15.88得到一个15.88%的对分比对象。
   */
  public Percent(String amountString) {
    if (isBlank(amountString)) {
      throw new IllegalArgumentException("amountString should not be blank");
    }
    // 默认2位小数，四舍五入
    this.amount = new BigDecimal(amountString).setScale(SCALE, RoundingMode.HALF_UP);
  }

  /**
   * 根据整数得到一个百分比对象，比如：传入15得到一个15%的对分比对象。
   */
  public Percent(int value) {
    this.amount = new BigDecimal(value).setScale(SCALE, RoundingMode.HALF_UP);
  }

  // ----------- 其他函数 --------------

  /**
   * 解析字符串得到一个百分比对象，比如：传入15.88得到一个15.88%的对分比对象。
   */
  public static Percent parse(String amountString) {
    return new Percent(amountString);
  }

  /**
   * 计算金额的百分比，即金额1除以金额2得到的百分比。
   */
  public static Percent getPercent(Money money1, Money money2) {
    BigDecimal percentValue = money1.getAmount().divide(money2.getAmount(), 4, RoundingMode.HALF_UP);
    return new Percent(percentValue.multiply(new BigDecimal(100)));
  }

  /**
   * 当前值是否为0
   */
  public boolean wasZero() {
    return equals(Percent.ZERO);
  }

  /**
   * 获取绝对值
   */
  public Percent abs() {
    return new Percent(this.amount.abs());
  }

  /**
   * 取负值
   */
  public Percent negate() {
    return new Percent(this.amount.negate());
  }

  /**
   * 加法
   */
  public Percent add(@NotNull Percent other) {
    return new Percent(this.amount.add(other.amount));
  }

  /**
   * 减法
   */
  public Percent subtract(@NotNull Percent other) {
    return new Percent(this.amount.subtract(other.amount));
  }

  /**
   * 计算金额的百分比
   */
  public Money getPercentValue(Money money) {
    if (money == null) {
      throw new IllegalArgumentException("money should not be null");
    }

    BigDecimal percentValue = this.amount.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
    return money.multiply(percentValue);
  }

  @Override
  public int compareTo(@NotNull Percent o) {
    return this.amount.compareTo(o.amount);
  }

  // ----------- toString --------------
  /**
   * 返回百分比的字符串表示，根据需要显示小数位数，例如15.00%得到结果15%。
   */
  @Override
  public String toString() {
    synchronized (PERCENT_DEFAULT_FORMAT) {
      return PERCENT_DEFAULT_FORMAT.format(amount) + "%";
    }
  }

  /**
   * 返回两位小数的百分比表示法，例如15.00%
   */
  public String toTwoDecimalString() {
    synchronized (TWO_DECIMAL_FORMAT) {
      return TWO_DECIMAL_FORMAT.format(amount) + "%";
    }
  }

  /**
   * 返回一位小数的百分比表示法，例如15.0%
   */
  public String toOneDecimalString() {
    synchronized (ONE_DECIMAL_FORMAT) {
      return ONE_DECIMAL_FORMAT.format(amount) + "%";
    }
  }

  /**
   * 返回不带小数的百分比表示法，例如15%
   */
  public String toNoneDecimalString() {
    synchronized (NONE_DECIMAL_FORMAT) {
      return NONE_DECIMAL_FORMAT.format(amount) + "%";
    }
  }

  // Jpa Converter的定义
  @javax.persistence.Converter(autoApply = true)
  public static class Converter implements AttributeConverter<Percent, BigDecimal> {
    @Override
    public BigDecimal convertToDatabaseColumn(Percent percent) {
      return percent == null
              ? null
              : percent.getAmount();
    }

    @Override
    public Percent convertToEntityAttribute(BigDecimal percentAmount) {
      return percentAmount == null
              ? null
              : new Percent(percentAmount);
    }
  }

  /**
   * 将Percent序列化为Json。
   */
  public static class Serializer extends JsonSerializer<Percent> {
    @Override
    public void serialize(Percent value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        gen.writeNull();
      }
      else {
        synchronized (PERCENT_DEFAULT_FORMAT) {
          gen.writeNumber(PERCENT_DEFAULT_FORMAT.format(value.getAmount()));
        }
      }
    }
  }

  /**
   * 将json反序列化为Percent。
   */
  public static class Deserializer extends JsonDeserializer<Percent> {
    @Override
    public Percent deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
      BigDecimal amount = p.getDecimalValue();
      return amount == null
              ? null
              : new Percent(amount);
    }
  }
}
