package com.tehang.common.utility.time;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 表示一个时间段，包含开始和截止日期。时区为北京时间，精确到天。
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public final class BjDateRange implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  /**
   * 开始日期, null表示开始日期无限制。
   */
  @JsonSerialize(using = BjDate.Serializer.class)
  @JsonDeserialize(using = BjDate.Deserializer.class)
  private BjDate from;

  /**
   * 结束日期, null表示结束日期无限制。结束日期可以和开始日期相同，这样就只表示那一天的范围。
   */
  @JsonSerialize(using = BjDate.Serializer.class)
  @JsonDeserialize(using = BjDate.Deserializer.class)
  private BjDate to;

  // ----------- 构造函数 --------------

  private BjDateRange() {
  }

  public BjDateRange(BjDate dateFrom, BjDate dateTo) {
    if (dateFrom != null && dateTo != null) {
      if (dateFrom.isAfter(dateTo)) {
        this.from = dateTo;
        this.to = dateFrom;
      }
    }

    this.from = dateFrom;
    this.to = dateTo;
  }

  // ----------- 其他函数 --------------

  /**
   * 创建一个时间段对象，参数可以为null, 表示无限制。
   */
  public static BjDateRange create(BjDate dateFrom, BjDate dateTo) {
    return new BjDateRange(dateFrom, dateTo);
  }

  /**
   * 当前时间段是否包含指定的日期？
   */
  public boolean contains(BjDate date) {
    if (date == null) {
      throw new IllegalArgumentException("date can not be null");
    }
    return (from == null || from.compareTo(date) <= 0)
        && (to == null || to.compareTo(date) >= 0);
  }

  /**
   * 当前的时间段是否和指定的时间段有重合？
   */
  public boolean overlapped(BjDateRange range) {
    return isBeforeOrEqual(this.from, range.to)
        && isBeforeOrEqual(range.from, this.to);
  }

  private static boolean isBeforeOrEqual(BjDate from, BjDate to) {
    if (from == null || to == null) {
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
