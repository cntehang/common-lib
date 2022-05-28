package com.tehang.common.utility.event.publish;

import brave.Tracer;
import com.tehang.common.utility.ApplicationContextProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * trace信息的一些辅助方法。
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraceInfoHelper {

  /**
   * 获取当前上下文中的traceId.
   */
  public static String getCurrentTraceId() {
    // 获取当前的springContext
    var applicationContext = ApplicationContextProvider.getApplicationContext();

    if (applicationContext == null) {
      return null;
    }

    Tracer tracer;
    try {
      // 获取当前上下文的tracer
      tracer = applicationContext.getBean(Tracer.class);
    }
    catch (NoSuchBeanDefinitionException ex) {
      // 未启用Tracer时，直接返回null
      log.debug("cannot found Tracer bean");
      return null;
    }

    if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
      var traceContext = tracer.currentSpan().context();
      var traceId = traceContext.traceId();

      if (traceId != 0) {
        log.debug("Exit getCurrentTraceId: {}", traceId);
        return String.valueOf(traceId);
      }
    }

    return null;
  }
}
