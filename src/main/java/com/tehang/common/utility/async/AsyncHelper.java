package com.tehang.common.utility.async;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步相关的辅助方法
 */
@Slf4j
public final class AsyncHelper {

  private AsyncHelper() {
    // do nothing
  }

  public static void async(Runnable runnable) {
    new Thread(() -> {
      try {
        runnable.run();

      } catch (Exception ex) {
        log.error("async task occurred error, msg: {}", ex.getMessage(), ex);
      }
    }).start();
  }

}
