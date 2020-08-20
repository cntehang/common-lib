package com.tehang.common.utility.functional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 监控相关的辅助方法
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public final class MonitorHelper {

  private static final Logger LOG = LoggerFactory.getLogger(MonitorHelper.class);

  private static final int ONE_SECOND_MILLS = 1000;

  private MonitorHelper() {
    // do nothing
  }

  /**
   * 性能监控，记录运行时间
   * 谨慎使用，代码侵入比较严重
   * 推荐场景：对每一行代码都需要监控运行时间，比如国内机票综合查询订单导出
   */
  public static <T> T monitorPerformance(String desc, Supplier<T> supplier) {

    long beginMills = System.currentTimeMillis();

    T result = supplier.get();

    double expendedSeconds = (double) (System.currentTimeMillis() - beginMills) / ONE_SECOND_MILLS;

    LOG.debug("monitorPerformance, {}: {}s", desc, String.format("%.1f", expendedSeconds));

    return result;
  }

  /**
   * 性能监控，记录运行时间
   * 谨慎使用，代码侵入比较严重
   * 推荐场景：对每一行代码都需要监控运行时间，比如国内机票综合查询订单导出
   */
  public static void monitorPerformance(String desc, Runnable runnable) {

    long beginMills = System.currentTimeMillis();

    runnable.run();

    double expendedSeconds = (double) (System.currentTimeMillis() - beginMills) / ONE_SECOND_MILLS;

    LOG.debug("monitorPerformance, {}: {}s", desc, String.format("%.2f", expendedSeconds));
  }

}
