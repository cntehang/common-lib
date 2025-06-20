package com.tehang.common.utility.rest;

import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.api.front.DataContainer;
import com.tehang.common.utility.rest.annotations.DisableWrapResponse;
import com.tehang.common.utility.rest.annotations.WrapResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 包装Controller的返回值的Advice.
 */
@RestControllerAdvice(annotations = {WrapResponse.class})
@Slf4j
public class RestControllerWrapResponseAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    if (returnType.hasMethodAnnotation(DisableWrapResponse.class)) {
      // 方法上如果有禁止包装的注解，则不进行包装
      return false;
    }
    return true;
  }

  @Override
  public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType,
    Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

    log.debug("Enter RestControllerWrapResponseAdvice.beforeBodyWrite");

    Object result;
    if (body instanceof DataContainer) {
      // 如果返回值已经包装过，就不再包装了
      log.debug("return object is DataContainer, wrap response is not required");
      result = body;
    }
    else if (body instanceof String) {
      // 需要手动封装原始的字符串返回值，并序列化为JSON字符串返回。原因如下：
      // Spring存在双重序列化问题：当原始控制器返回String，而我们在`beforeBodyWrite`中返回一个非String对象（如`DataContainer`）时，Spring会尝试用`StringHttpMessageConverter`来转换这个非String对象，导致失败。
      // 解决方案：对于原始返回值为String的情况，我们手动将其转换为JSON字符串（即返回一个String），这样Spring就会直接使用`StringHttpMessageConverter`将其写入响应，而不会尝试再次转换。
      result = JsonUtils.toJson(new DataContainer(0, StringUtils.EMPTY, body));
    }
    else {
      // 其他所有情况，都会包装
      result = new DataContainer(0, StringUtils.EMPTY, body);
      log.debug("wrap response completed");
    }

    log.debug("Exit RestControllerWrapResponseAdvice.beforeBodyWrite");
    return result;
  }
}