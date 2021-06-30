package com.tehang.common.utility.async;

import brave.Tracing;
import lombok.extern.slf4j.Slf4j;

/**
 * 异步相关的辅助方法.
 */
@Slf4j
public final class AsyncHelper {

  private static final String DEFAULT_SPAN_NAME = "async";

  private AsyncHelper() {
    // do nothing
  }

  /**
   * async.
   *
   * @param runnable runnable
   */
  public static void async(Runnable runnable) {
    new Thread(() -> {
      try {
        runnable.run();

      }
      catch (Exception ex) {
        log.error("async task occurred error, msg: {}", ex.getMessage(), ex);
      }
    }).start();
  }

  /**
   * async.
   *
   * @param tracing  tracing
   * @param runnable runnable
   */
  public static void async(Tracing tracing, Runnable runnable) {
    var tracer = tracing.tracer();
    var parent = tracing.currentTraceContext().get();

    new Thread(() -> {
      var span = tracer.startScopedSpanWithParent(DEFAULT_SPAN_NAME, parent);

      try {
        runnable.run();

      }
      catch (Exception ex) {
        log.error("async task occurred error, msg: {}", ex.getMessage(), ex);

      }
      finally {
        span.finish();
      }
    }).start();
  }

}
