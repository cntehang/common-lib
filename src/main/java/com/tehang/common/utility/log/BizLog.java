package com.tehang.common.utility.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法调用的业务日志，标记在方法上，用来记录对应方法的调用日志，包括方法的耗时等，一般用来记录供应商接口的调用情况。
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizLog {

  String value() default "";
  
  /**
   * 日志标签，可以指定多个, 方便根据标签搜索
   */
  String[] tags() default {};
}