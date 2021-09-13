package com.tehang.common.utility.async;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 用来记录异步方法异常的Aspect类
 */
@Slf4j
@Aspect
@Component
public class AsyncExceptionLoggerAspect {

  /**
   * 通过around方式，在目标方法发生异常时记录日志
   *
   * @param joinPoint
   * @return
   * @throws Throwable
   */
  @Around("@annotation(com.tehang.common.utility.async.AsyncExceptionLogger)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      return joinPoint.proceed();

    } catch (Exception ex) {
      log.error("async methods exception happens, msg: {}", ex.getMessage(), ex);
      throw ex;
    }
  }
}