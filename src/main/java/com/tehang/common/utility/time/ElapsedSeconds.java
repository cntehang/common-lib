package com.tehang.common.utility.time;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 表示一个时间范围，以秒为单位。常用于日志记录时获取花费的时间。
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElapsedSeconds implements Serializable {

  private static final long serialVersionUID = -5962799069942105993L;

  private BjTime start;
  private BjTime end;

  /**
   * 创建一个时间范围，从指定的开始时间到当前时间为止。
   */
  public static ElapsedSeconds from(BjTime start) {
    return create(start, BjTime.now());
  }

  public static ElapsedSeconds create(BjTime start, BjTime end) {
    var result = new ElapsedSeconds();
    result.start = Objects.requireNonNull(start, "start must not be null");
    result.end = Objects.requireNonNull(end, "end must not be null");
    return result;
  }

  /**
   * 获取该时间范围的字符串表示，以秒为单位，保留一位小数。
   */
  @Override
  public String toString() {
    return String.format("%.1f", getSeconds());
  }

  /**
   * 获取该时间范围的秒数
   */
  public double getSeconds() {
    return ((double)end.getInnerTime().getMillis() - start.getInnerTime().getMillis()) / 1000;
  }
}
