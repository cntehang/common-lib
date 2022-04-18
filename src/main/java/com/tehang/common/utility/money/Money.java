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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 表示金额，精确到两位小数（不包含币种，仅表示金额的数值）。
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
@JsonSerialize(using = Money.Serializer.class)
@JsonDeserialize(using = Money.Deserializer.class)
public final class Money implements Serializable, Comparable<Money> {

  private static final long serialVersionUID = -5962799069942105993L;

  private static final DecimalFormat MONEY_DEFAULT_FORMAT = new DecimalFormat("#.##");
  private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");
  private static final int SCALE = 2;

  public static final Money ZERO = new Money();

  // ----------- 内部字段 --------------
  /**
   * 内部持有的金额的数值
   */
  private final BigDecimal amount;

  // ----------- 构造函数 --------------

  private Money() {
    // 私有的默认构造函数，默认为0
    this(BigDecimal.ZERO);
  }

  /**
   * 根据指定的BigDecimal初始化一个金额对象
   */
  public Money(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("amount should not be null");
    }
    // 默认2位小数，四舍五入
    this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
  }

  public Money(String amountString) {
    if (isBlank(amountString)) {
      throw new IllegalArgumentException("amountString should not be blank");
    }
    // 默认2位小数，四舍五入
    this.amount = new BigDecimal(amountString).setScale(SCALE, RoundingMode.HALF_UP);
  }

  public Money(long value) {
    this.amount = new BigDecimal(value).setScale(SCALE, RoundingMode.HALF_UP);
  }

  // ----------- 其他函数 --------------

  /**
   * 解析金额字符串
   */
  public static Money parse(String amountString) {
    return new Money(amountString);
  }

  /**
   * 当前值是否为0
   */
  public boolean wasZero() {
    return equals(Money.ZERO);
  }

  /**
   * 获取绝对值
   */
  public Money abs() {
    return new Money(this.amount.abs());
  }

  /**
   * 取负值
   */
  public Money negate() {
    return new Money(this.amount.negate());
  }

  /**
   * 加法
   */
  public Money add(@NotNull Money other) {
    return new Money(this.amount.add(other.amount));
  }

  /**
   * 减法
   */
  public Money subtract(@NotNull Money other) {
    return new Money(this.amount.subtract(other.amount));
  }

  /**
   * 乘法：乘以一个整数
   */
  public Money multiply(@NotNull int multiplicand) {
    return new Money(this.amount.multiply(new BigDecimal(multiplicand)));
  }

  /**
   * 乘法：乘以一个十进制数
   */
  public Money multiply(@NotNull BigDecimal multiplicand) {
    return new Money(this.amount.multiply(multiplicand));
  }

  /**
   * 求和
   */
  public static Money sum(Money... nums) {
    return Arrays.stream(nums)
            .filter(Objects::nonNull)
            .reduce(Money.ZERO, Money::add);
  }

  /**
   * 将指定的对象中的属性值相加
   */
  public static <T> Money sumBy(List<T> items, Function<T, Money> evaluator) {
    return items.stream()
            .map(evaluator)
            .filter(Objects::nonNull)
            .reduce(Money.ZERO, Money::add);
  }

  /**
   * 将指定的对象进行过滤，然后累加指字的字段值
   */
  public static <T> Money sumBy(List<T> items, Predicate<? super T> predicate, Function<T, Money> evaluator) {
    return items.stream()
            .filter(predicate)
            .map(evaluator)
            .filter(Objects::nonNull)
            .reduce(Money.ZERO, Money::add);
  }

  @Override
  public int compareTo(@NotNull Money o) {
    return this.amount.compareTo(o.amount);
  }

  // ----------- toString --------------
  @Override
  public String toString() {
    synchronized (MONEY_DEFAULT_FORMAT) {
      return MONEY_DEFAULT_FORMAT.format(amount);
    }
  }

  /**
   * 返回两位小数的金额表示法
   */
  public String toTwoDecimalString() {
    synchronized (TWO_DECIMAL_FORMAT) {
      return TWO_DECIMAL_FORMAT.format(amount);
    }
  }


  // Jpa Converter的定义
  public static class Converter implements AttributeConverter<Money, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Money money) {
      return money == null
              ? null
              : money.getAmount();
    }

    @Override
    public Money convertToEntityAttribute(BigDecimal amount) {
      return amount == null
              ? null
              : new Money(amount);
    }
  }

  /**
   * 将Money序列化为Json。
   */
  public static class Serializer extends JsonSerializer<Money> {
    @Override
    public void serialize(Money value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        gen.writeNull();
      }
      else {
        gen.writeNumber(value.toString());
      }
    }
  }

  /**
   * 将json反序列化为Money。
   */
  public static class Deserializer extends JsonDeserializer<Money> {
    @Override
    public Money deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
      BigDecimal amount = p.getDecimalValue();
      return amount == null
              ? null
              : new Money(amount);
    }
  }
}
