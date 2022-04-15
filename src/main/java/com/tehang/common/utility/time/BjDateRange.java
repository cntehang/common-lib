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
  private BjDate start;

  /**
   * 结束日期, null表示结束日期无限制。结束日期可以和开始日期相同，这样就只表示那一天的范围。
   */
  @JsonSerialize(using = BjDate.Serializer.class)
  @JsonDeserialize(using = BjDate.Deserializer.class)
  private BjDate end;

  // ----------- 构造函数 --------------

  private BjDateRange() {
  }

  public BjDateRange(BjDate dateStart, BjDate dateEnd) {
    if (dateStart != null && dateEnd != null) {
      if (dateStart.isAfter(dateEnd)) {
        this.start = dateEnd;
        this.end = dateStart;
      }
    }

    this.start = dateStart;
    this.end = dateEnd;
  }

  // ----------- 其他函数 --------------

  /**
   * 创建一个时间段对象，参数可以为null, 表示无限制。
   */
  public static BjDateRange create(BjDate dateStart, BjDate dateEnd) {
    return new BjDateRange(dateStart, dateEnd);
  }

  /**
   * 当前时间段是否包含指定的日期？
   */
  public boolean contains(BjDate date) {
    if (date == null) {
      throw new IllegalArgumentException("date can not be null");
    }
    return (start == null || start.compareTo(date) <= 0)
        && (end == null || end.compareTo(date) >= 0);
  }

  /**
   * 当前的时间段是否和指定的时间段有重合？
   */
  public boolean overlapped(BjDateRange range) {
    return isBeforeOrEqual(this.start, range.end)
        && isBeforeOrEqual(range.start, this.end);
  }

  private static boolean isBeforeOrEqual(BjDate start, BjDate end) {
    if (start == null || end == null) {
      return true;
    }
    return start.compareTo(end) <= 0;
  }

  // ----------- toString --------------
  @Override
  public String toString() {
    return String.format("(%s, %s)", start, end);
  }
}
