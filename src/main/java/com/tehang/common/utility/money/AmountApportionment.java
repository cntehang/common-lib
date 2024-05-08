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
   * 金额分摊算法。按指定的比例进行分摊，返回分摊后的金额列表。
   * @param totalAmount 待分摊的总金额，不能为null, 也不能为负数
   * @param ratios 分摊的系数列表，不能为空，也不能为负数
   * @return 分摊后的金额列表
   */
  public static List<Money> apportion(final @NotNull Money totalAmount, final @NotEmpty List<Money> ratios) {
    if (totalAmount == null || totalAmount.lessThan(Money.ZERO)) {
      throw new IllegalArgumentException("无效的totalAmount: " + totalAmount);
    }

    if (isEmpty(ratios)) {
      throw new IllegalArgumentException("ratios不能为空");
    }

    for (Money ratio : ratios) {
      if (ratio == null || ratio.lessThan(Money.ZERO)) {
        throw new IllegalArgumentException("无效的ratio值: " + ratio);
      }
    }

    // 金额拆分：转换为long类型进行拆分
    var longResults = doApportion(toCents(totalAmount.getAmount()), getLongRatiosForMoney(ratios));

    // 将拆分后的long类型金额转换为Money类型
    return longResults.stream()
        .map(Money::parseByCents)
        .collect(toList());
  }

  /**
   * 金额分摊算法。按指定的比例进行分摊，返回分摊后的金额列表。
   * @param totalAmount 待分摊的总金额，不能为null, 也不能为负数
   * @param ratios 分摊的系数列表，不能为空，也不能为负数
   * @return 分摊后的金额列表
   */
  public static List<BigDecimal> apportion(final @NotNull BigDecimal totalAmount, final @NotEmpty List<BigDecimal> ratios) {
    if (totalAmount == null || BigDecimalUtils.lessThanZero(totalAmount)) {
      throw new IllegalArgumentException("无效的totalAmount: " + totalAmount);
    }

    if (isEmpty(ratios)) {
      throw new IllegalArgumentException("ratios不能为空");
    }

    for (BigDecimal ratio : ratios) {
      if (ratio == null || BigDecimalUtils.lessThanZero(ratio)) {
        throw new IllegalArgumentException("无效的ratio值: " + ratio);
      }
    }

    // 金额拆分：转换为long类型进行拆分
    var longResults = doApportion(toCents(totalAmount), getLongRatiosForDecimal(ratios));

    // 将拆分后的long类型金额转换为BigDecimal类型
    return longResults.stream()
        .map(item -> new BigDecimal(item).divide(HUNDRED, 2, RoundingMode.HALF_UP))
        .collect(toList());
  }

  /**
   * 金额分摊算法。按指定的比例进行分摊，返回分摊后的金额列表。
   * @param totalAmount 待分摊的总金额，不能为负数
   * @param ratios 分摊的系数列表，不能为空，也不能为负数
   * @return 分摊后的金额列表
   */
  public static List<Long> apportion(long totalAmount, List<Long> ratios) {
    if (totalAmount < 0) {
      throw new IllegalArgumentException("totalAmount不能为负数");
    }
    if (isEmpty(ratios)) {
      throw new IllegalArgumentException("ratios不能为空");
    }

    for (Long ratio : ratios) {
      if (ratio == null || ratio < 0) {
        throw new IllegalArgumentException("无效的ratio值: " + ratio);
      }
    }

    return doApportion(totalAmount, ratios);
  }

  private static List<Long> doApportion(long totalAmount, List<Long> ratios) {
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

  private static List<Long> getLongRatiosForMoney(List<Money> ratios) {
    return ratios.stream()
        .map(item -> toCents(item.getAmount()))
        .collect(toList());
  }

  /** 将货币转换为分。即乘以100，然后四舍五入取整。*/
  private static Long toCents(BigDecimal decimal) {
    return decimal.multiply(HUNDRED)
        .setScale(0, RoundingMode.HALF_UP)
        .longValue();
  }
}
