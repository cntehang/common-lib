package com.tehang.common.utility;

import com.tehang.common.infrastructure.exceptions.ParameterException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * 使用 BigDecimal 类时用到的工具方法
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BigDecimalUtils {

  private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("0");
  private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");
  private static final DecimalFormat THREE_DECIMAL_FORMAT = new DecimalFormat("0.000");

  /**
   * 100 BigDecimal，计算百分比时使用
   */
  private static final BigDecimal HUNDRED = new BigDecimal(100);

  /**
   * 做除法时保留四位小数, 转换为百分数时就是2为小数
   */
  private static final int SCALE_FOUR = 4;

  /**
   * 判断两个BigDecimal的值是否相等？
   */
  public static boolean valueEquals(BigDecimal num1, BigDecimal num2) {
    return num1 != null
        && num2 != null
        && num1.compareTo(num2) == 0;
  }

  /**
   * 获取BigDecimal的值，如果为null, 则返回BigDecimal.Zero
   */
  public static BigDecimal valueOrZero(BigDecimal value) {
    return value == null ?
        BigDecimal.ZERO :
        value;
  }

  /**
   * 不为0
   */
  public static boolean isNotZero(BigDecimal value) {
    return value != null && value.compareTo(BigDecimal.ZERO) != 0;
  }

  /**
   * 是否为0
   */
  public static boolean wasZero(BigDecimal value) {
    return value != null && value.compareTo(BigDecimal.ZERO) == 0;
  }

  /**
   * 传入一个数字，返回值为 0 和该值之间的较大者
   *
   * @param number 传入的数字
   * @return zero if number < zero, or else number
   */
  public static BigDecimal getNumberNoLessThanZero(BigDecimal number) {
    return number.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : number;
  }

  /**
   * 传入一个BigDecimal和Integer，计算两者的乘积
   *
   * @param multiplier   乘数
   * @param multiplicand 被乘数
   * @return
   */
  public static BigDecimal multiplyInteger(BigDecimal multiplier, Integer multiplicand) {
    return multiplier.multiply(new BigDecimal(multiplicand));
  }

  /**
   * 将一个 BigDecimal 数字格式化为整数字符串返回
   *
   * @param num 源数字
   * @return 对应的整数字符串
   */
  public static String formatAsInteger(BigDecimal num) {
    synchronized (INTEGER_FORMAT) {
      return INTEGER_FORMAT.format(num);
    }
  }

  /**
   * 将一个 BigDecimal 数字格式化为保留两位小数返回
   *
   * @param num 源数字
   * @return 保留两位小数
   */
  public static String formatAsTwoDecimal(BigDecimal num) {
    if (num == null) {
      // 兼容null值
      return null;
    }

    synchronized (TWO_DECIMAL_FORMAT) {
      return TWO_DECIMAL_FORMAT.format(num);
    }
  }

  /**
   * 将一个 BigDecimal 数字格式化为保留三位小数返回
   *
   * @param num 源数字
   * @return 保留三位小数
   */
  public static String formatAsThreeDecimal(BigDecimal num) {
    synchronized (THREE_DECIMAL_FORMAT) {
      return THREE_DECIMAL_FORMAT.format(num);
    }
  }

  /**
   * 计算百分比，保留2位
   *
   * @param percent 要计算的百分比
   * @return percent % 的小数形式
   */
  public static BigDecimal divideByHundred(String percent) {
    return new BigDecimal(percent).divide(HUNDRED, 2, RoundingMode.UNNECESSARY);
  }

  /**
   * 计算一个数除以另一个数, 四舍五入, 保留4位小数, 再转换为百分数
   * 如: 1/3 = 0.3333 = 33.33%
   *
   * @param value1 被除数
   * @param value2 除数
   * @return 百分数
   */
  public static BigDecimal divideWithHalfUpToPercent(BigDecimal value1, BigDecimal value2) {
    if (equals(BigDecimal.ZERO, value2)) {
      throw new ParameterException("除数不能为0");
    }
    return value1.divide(value2, SCALE_FOUR, RoundingMode.HALF_UP).multiply(HUNDRED);
  }

  /**
   * 两数相减
   *
   * @param value1 减数
   * @param value2 被减数
   * @return
   */
  public static BigDecimal subtract(BigDecimal value1, BigDecimal value2) {
    return value1.subtract(value2);
  }

  public static BigDecimal subtractDefaultZero(BigDecimal value1, BigDecimal value2) {
    return Optional.ofNullable(value1).orElse(BigDecimal.ZERO)
            .subtract(Optional.ofNullable(value2).orElse(BigDecimal.ZERO));
  }

  /**
   * 两数相加
   */
  public static BigDecimal add(BigDecimal value1, BigDecimal value2) {
    return value1.add(value2);
  }

  public static BigDecimal addDefaultZero(BigDecimal value1, BigDecimal value2) {
    return Optional.ofNullable(value1).orElse(BigDecimal.ZERO)
            .add(Optional.ofNullable(value2).orElse(BigDecimal.ZERO));
  }

  /**
   * 判断两个数是否相等
   *
   * @param value1 第一个数
   * @param value2 第二个数
   * @return 是否相等
   */
  public static boolean equals(BigDecimal value1, BigDecimal value2) {
    return value1.compareTo(value2) == 0;
  }

  /**
   * 判断两个数是否不相等
   *
   * @param value1 第一个数
   * @param value2 第二个数
   * @return 是否不相等
   */
  public static boolean notEquals(BigDecimal value1, BigDecimal value2) {
    return !equals(value1, value2);
  }

  /**
   * 多个数相加
   *
   * @param nums
   * @return
   */
  public static BigDecimal sum(BigDecimal... nums) {
    return Arrays.stream(nums)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * 多个数相加
   *
   * @param nums
   * @return
   */
  public static BigDecimal sum(List<BigDecimal> nums) {
    BigDecimal result = BigDecimal.ZERO;
    if (CollectionUtils.isNotEmpty(nums)) {
      result = nums.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }
    return result;
  }

  /**
   * 判断值是否大于0
   *
   * @param num 要比较的值
   * @return 比较结果
   */
  public static boolean greaterThanZero(String num) {
    return new BigDecimal(num).compareTo(BigDecimal.ZERO) > 0;
  }

  /**
   * 判断值是否大于0
   *
   * @param num 要比较的值
   * @return 比较结果
   */
  public static boolean greaterThanZero(BigDecimal num) {
    return valueOrZero(num).compareTo(BigDecimal.ZERO) > 0;
  }

  /**
   * 判断值是否小于0
   *
   * @param num 要比较的值
   * @return 比较结果
   */
  public static boolean lessThanZero(String num) {
    return new BigDecimal(num).compareTo(BigDecimal.ZERO) < 0;
  }

  /**
   * 判断值是否小于0
   *
   * @param num 要比较的值
   * @return 比较结果
   */
  public static boolean lessThanZero(BigDecimal num) {
    return valueOrZero(num).compareTo(BigDecimal.ZERO) < 0;
  }

  /**
   * 将指定的对象中的属性值相加
   */
  public static <T> BigDecimal sumBy(List<T> items, Function<T, BigDecimal> evaluator) {
    return items.stream()
        .map(evaluator)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * 是否为数字
   *
   * @param number 数字字符串
   * @return true 若为正确的数字
   */
  public static boolean isCorrectFormattedNumber(String number) {
    log.debug("Check is number: {} correct formatted", number);

    boolean result = true;

    try {
      new BigDecimal(number);
      log.debug("Number: {} is correct formatted", number);
    } catch (NumberFormatException ex) {
      log.debug("Number: {} is not correct formatted", number);
      result = false;
    }

    return result;
  }

  /**
   * 大于
   */
  public static boolean greater(BigDecimal value1, BigDecimal value2) {
    if (value1 == null || value2 == null) {
      throw new ParameterException("参数不能为空");
    }
    return value1.compareTo(value2) > 0;
  }

  /**
   * 大于或等于
   */
  public static boolean greaterOrEqual(BigDecimal value1, BigDecimal value2) {
    if (value1 == null || value2 == null) {
      throw new ParameterException("参数不能为空");
    }
    return value1.compareTo(value2) > -1;
  }

  /**
   * 小于
   */
  public static boolean less(BigDecimal value1, BigDecimal value2) {
    if (value1 == null || value2 == null) {
      throw new ParameterException("参数不能为空");
    }
    return value1.compareTo(value2) < 0;
  }

  /**
   * 小于或等于
   */
  public static boolean lessOrEqual(BigDecimal value1, BigDecimal value2) {
    if (value1 == null || value2 == null) {
      throw new ParameterException("参数不能为空");
    }
    return value1.compareTo(value2) < 1;
  }

  /**
   * 将以BigDecimal表示的货币金额转换为以分表示，即乘以100，然后取整
   */
  public static long toCents(BigDecimal value) {
    if (value == null) {
      throw new ParameterException("参数不能为空: value");
    }

    return value.multiply(new BigDecimal(100))
        .setScale(0, RoundingMode.HALF_UP)
        .longValue();
  }

  /**
   * 四舍五入
   */
  public static BigDecimal floor(BigDecimal price, int scale) {
    return price.setScale(scale, RoundingMode.HALF_UP);
  }
}
