package com.tehang.common.utility.money;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * 表示一个价格范围，为闭区间。
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class MoneyRange implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  /**
   * 最低价格, null表示最低价格无限制。
   */
  @ApiModelProperty(value = "最低价格, null表示最低价格无限制", dataType = "java.math.BigDecimal", example = "200.00")
  @Column(columnDefinition = "DECIMAL(19, 2) NULL")
  private Money from;

  /**
   * 最高价格, null表示最高价格无限制。该价格段为闭区间。
   */
  @ApiModelProperty(value = "最高价格, null表示最高价格无限制。该价格段为闭区间", dataType = "java.math.BigDecimal", example = "300.00")
  @Column(columnDefinition = "DECIMAL(19, 2) NULL")
  private Money to;

  // ----------- 构造函数 --------------

  protected MoneyRange() {
    // 保留此空构造函数，以方便一些框架使用
  }

  public MoneyRange(Money moneyFrom, Money moneyTo) {
    if (moneyFrom != null && moneyTo != null) {
      if (moneyFrom.compareTo(moneyTo) > 0) {
        this.from = moneyTo;
        this.to = moneyFrom;
        return;
      }
    }

    this.from = moneyFrom;
    this.to = moneyTo;
  }

  // ----------- 其他函数 --------------

  /**
   * 创建一个价格范围对象，参数可以为null, 表示无限制。
   */
  public static MoneyRange create(Money moneyFrom, Money moneyTo) {
    return new MoneyRange(moneyFrom, moneyTo);
  }

  /**
   * 当前价格范围是否包含指定的价格？
   */
  public boolean contains(Money money) {
    if (money == null) {
      throw new IllegalArgumentException("money can not be null");
    }
    return (from == null || from.compareTo(money) <= 0)
        && (to == null || to.compareTo(money) >= 0);
  }

  /**
   * 当前的价格范围是否和指定的价格范围有重合？
   */
  public boolean overlapped(MoneyRange range) {
    return isLessOrEqual(this.from, range.to)
        && isLessOrEqual(range.from, this.to);
  }

  private static boolean isLessOrEqual(Money from, Money to) {
    if (from == null) {
      return true;
    }
    if (to == null) {
      return true;
    }
    return from.compareTo(to) <= 0;
  }

  // ----------- toString --------------
  @Override
  public String toString() {
    return String.format("(%s, %s)", from, to);
  }
}
