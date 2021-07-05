package com.tehang.common.utility.rest.annotations;

import com.tehang.common.utility.rest.RestControllerWrapExceptionAdvice;
import com.tehang.common.utility.rest.RestControllerWrapResponseAdvice;
import com.tehang.common.utility.rest.WebConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用Controller的Response包装功能，包括DataContainer包装和异常处理的包装.
 *
 * <p>用法： 1. 在需要包装的Controller上添加WrapResponse, WrapException注解, 一般加在Controller的基类上； 2. 在不需要包装的Controller方法上添加DisableWrapResponse; 3.
 * 在Application.java上添加本注解：EnableWrapResponseAndException
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RestControllerWrapResponseAdvice.class, RestControllerWrapExceptionAdvice.class, WebConfig.class})
public @interface EnableWrapResponseAndException {

}
