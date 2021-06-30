package com.tehang.common.utility.rest;

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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 包装Controller的返回值的Advice.
 */
@RestControllerAdvice(annotations = {WrapResponse.class})
@Slf4j
public class RestControllerWrapResponseAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
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
    else if (disableWrapResponse()) {
      // 如果请求中有禁止包装的标记，也不再包装了
      log.debug("method marked as DisableWrapResponse, wrap response is not required");
      result = body;
    }
    else {
      // 其他所有情况，都会包装
      result = new DataContainer(0, StringUtils.EMPTY, body);
      log.debug("wrap response completed");
    }

    log.debug("Exit RestControllerWrapResponseAdvice.beforeBodyWrite");
    return result;
  }

  private boolean disableWrapResponse() {
    // 获取请求
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      // 获取servletRequest
      var servletRequest = requestAttributes.getRequest();

      // 判断请求是否有包装标记
      String disableWrapResponseValue = (String) servletRequest.getAttribute(DisableWrapResponse.class.getSimpleName());
      return StringUtils.equalsIgnoreCase(disableWrapResponseValue, "true");
    }
    return false;
  }
}