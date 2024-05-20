package com.tehang.common.utility.money;

import com.tehang.common.utility.BigDecimalUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.tehang.common.utility.CollectionUtils.isEmpty;
import static java.util.stream.Collectors.toList;

/** 金额分摊的辅助类。*/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AmountApportionment {

  private static final BigDecimal HUNDRED = new BigDecimal(100);

  /**
   * 金额分摊算法。按指定的比例进行分摊，返回分摊后的金额列表。（当不能精确分摊时，余数将分摊到最后一项中）
   * @param totalAmount 待分摊的总金额，不能为null
   * @param ratios 分摊的系数列表，不能为空
   * @return 分摊后的金额列表
   */
  public static List<Money> apportion(final @NotNull Money totalAmount, final @NotEmpty List<Money> ratios, @NotNull ApportionPrecision precision) {
    if (totalAmount == null) {
      throw new IllegalArgumentException("totalAmount不能为null");
    }

    if (isEmpty(ratios)) {
      throw new IllegalArgumentException("ratios不能为空");
    }

    for (Money ratio : ratios) {
      if (ratio == null) {
        throw new IllegalArgumentException("ratio值不能为null");
      }
    }

    if (precision == null) {
      throw new IllegalArgumentException("金额分摊精度不能为空");
    }

    List<BigDecimal> ratiosOfDecimal = ratios.stream()
        .map(money -> money.getAmount().abs())
        .collect(toList());

    // 分摊金额
    List<BigDecimal> result = doApportionForBigDecimal(totalAmount.getAmount().abs(), ratiosOfDecimal, precision);

    if (totalAmount.lessThan(Money.ZERO)) {
      // 处理负数
      return result.stream()
          .map(amount -> new Money(amount.negate()))
          .collect(toList());
    }
    else {
      return result.stream()
          .map(Money::new)
          .collect(toList());
    }
  }

  /**
   * 金额分摊算法。按指定的比例进行分摊，返回分摊后的金额列表。（当不能精确分摊时，余数将分摊到最后一项中）
   * @param totalAmount 待分摊的总金额，不能为null
   * @param ratios 分摊的系数列表，不能为空
   * @return 分摊后的金额列表
   */
  public static List<BigDecimal> apportion(final @NotNull BigDecimal totalAmount, final @NotEmpty List<BigDecimal> ratios, @NotNull ApportionPrecision precision) {
    if (totalAmount == null) {
      throw new IllegalArgumentException("totalAmount不能为null");
    }

    if (isEmpty(ratios)) {
      throw new IllegalArgumentException("ratios不能为空");
    }

    for (BigDecimal ratio : ratios) {
      if (ratio == null) {
        throw new IllegalArgumentException("ratio值不能为null");
      }
    }

    if (precision == null) {
      throw new IllegalArgumentException("金额分摊精度不能为空");
    }

    // 进行分摊: 系数取绝对值
    var result = doApportionForBigDecimal(totalAmount.abs(), ratios.stream().map(BigDecimal::abs).collect(toList()), precision);

    if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
      // 处理负数
      return result.stream()
          .map(BigDecimal::negate)
          .collect(toList());
    }
    else {
      return result;
    }
  }

  private static List<BigDecimal> doApportionForBigDecimal(BigDecimal totalAmount, List<BigDecimal> ratios, ApportionPrecision precision) {
    if (precision == ApportionPrecision.Cent) {
      // 金额拆分：转换为long类型进行拆分
      var longResults = doApportionForLong(toCents(totalAmount), getLongRatiosForDecimal(ratios));

      // 将拆分后的long类型金额转换为BigDecimal类型
      return longResults.stream()
          .map(item -> new BigDecimal(item).divide(HUNDRED, 2, RoundingMode.HALF_UP))
          .collect(toList());
    }
    else {
      // 获取金额的整数部分，以元为单位
      long integerPart = totalAmount.longValue();

      // 先将金额的整数部分进行拆分
      var integerResults = doApportionForLong(integerPart, getLongRatiosForDecimal(ratios));

      // 将拆分后的long类型金额转换为BigDecimal类型
      List<BigDecimal> result = integerResults.stream()
          .map(BigDecimal::new)
          .collect(toList());

      // 获取金额的余数部分, 添加到最后一项中
      var fractionalPart = totalAmount.subtract(new BigDecimal(integerPart));
      if (!BigDecimalUtils.wasZero(fractionalPart)) {
        if (integerPart != 0) {
          for (int i = result.size() - 1; i >= 0; i--) {
            if (!BigDecimalUtils.wasZero(result.get(i))) {
              // 将小数部分添加到最后一项不为0的项中
              result.set(i, result.get(i).add(fractionalPart));
              break;
            }
          }
        }
        else {
          // 如果整数部分为0，则将小数部分添加到最后一项中
          result.set(result.size() - 1, result.get(result.size() - 1).add(fractionalPart));
        }
      }
      return result;
    }
  }

  /**
   * 金额分摊算法。按指定的比例进行分摊，返回分摊后的金额列表（当不能精确分摊时，余数将分摊到最后一项中）。
   * @param totalAmount 待分摊的总金额
   * @param ratios 分摊的系数列表，不能为空
   * @return 分摊后的金额列表
   */
  public static List<Long> apportion(long totalAmount, List<Long> ratios) {
    if (isEmpty(ratios)) {
      throw new IllegalArgumentException("ratios不能为空");
    }

    for (Long ratio : ratios) {
      if (ratio == null) {
        throw new IllegalArgumentException("ratio值不能为null");
      }
    }

    // 进行分摊
    var result = doApportionForLong(Math.abs(totalAmount), ratios.stream().map(Math::abs).collect(toList()));

    if (totalAmount < 0) {
      // 处理负数
      return result.stream()
          .map(item -> -item)
          .collect(toList());
    }
    else {
      return result;
    }
  }

  private static List<Long> doApportionForLong(long totalAmount, List<Long> ratios) {
    // 计算总的分摊比例
    long sumRatio = 0;
    for (long ratio : ratios) {
      sumRatio += ratio;
    }

    // 创建一个用于存放分摊金额的列表
    List<Long> apportionedAmounts = new ArrayList<>();

    // 分摊金额, 并累加已分摊的总金额
    long totalApportionedAmount = 0;
    for (long ratio : ratios) {
      // 计算分摊金额（以分为单位）
      long apportionedAmount;

      if (sumRatio == 0) {
        // 如果分摊系数全为0，则平均分摊
        apportionedAmount = totalAmount / ratios.size();
      }
      else {
        apportionedAmount = totalAmount * ratio / sumRatio;
      }
      apportionedAmounts.add(apportionedAmount);
      totalApportionedAmount += apportionedAmount;
    }

    // 分摊可能会造成精度损失，将差值添加到最后一个分摊系数不为0的分摊金额中
    long adjustment = totalAmount - totalApportionedAmount;
    if (adjustment != 0) {
      for (int i = ratios.size() - 1; i >= 0; i--) {
        if (ratios.get(i) != 0) {
          // 对最后一个不为0的分摊金额进行调整
          apportionedAmounts.set(i, apportionedAmounts.get(i) + adjustment);
          break;
        }
      }
    }

    return apportionedAmounts;
  }

  private static List<Long> getLongRatiosForDecimal(List<BigDecimal> ratios) {
    return ratios.stream()
        .map(AmountApportionment::toCents)
        .collect(toList());
  }

  /** 将货币转换为分。即乘以100，然后四舍五入取整。*/
  private static Long toCents(BigDecimal decimal) {
    return decimal.multiply(HUNDRED)
        .setScale(0, RoundingMode.HALF_UP)
        .longValue();
  }
}
