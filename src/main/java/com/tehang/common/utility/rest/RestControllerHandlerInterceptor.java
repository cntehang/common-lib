package com.tehang.common.utility.rest;

import com.tehang.common.utility.rest.annotations.DisableWrapResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 所有RestController执行拦截器.
 */
@Slf4j
public class RestControllerHandlerInterceptor implements HandlerInterceptor {

  /**
   * 在进入方法处理之前，拦截标注了@DisableWrapResponse注解的请求，并设置相应标记.
   **/
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    log.debug("Enter RestControllerHandlerInterceptor.preHandle");

    if (handler instanceof HandlerMethod) {
      // 获取请求的方法
      HandlerMethod handlerMethod = (HandlerMethod) handler;
      Method method = handlerMethod.getMethod();

      // 根据方法上的DisableWrapResponse标记, 在http请求中相应属性，将来在RestControllerResponseAdvice中根据此属性决定是否包装返回值
      if (method.isAnnotationPresent(DisableWrapResponse.class)) {
        // 在请求中设置标记
        request.setAttribute(DisableWrapResponse.class.getSimpleName(), "true");
        log.debug("set DisableWrapResponse=true in request");
      }
    }
    return true;
  }
}