package com.tehang.common.infrastructure.filters;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理跨域请求的filter.
 */
@Component
@Order(Integer.MIN_VALUE)
public class CorsFilter implements Filter {

  /**
   * init.
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // do nothing.
  }

  /**
   * 处理跨域请求.
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET_CORP_INSURANCE_LIST, OPTIONS, DELETE, PUT");
      httpResponse.setHeader("Access-Control-Max-Age", "3600");
      httpResponse.setHeader("Access-Control-Allow-Origin", "*");
      //httpResponse.addHeader("Access-Control-Allow-Headers", "*");
      httpResponse.setHeader("Access-Control-Allow-Headers",
        "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, "
          + "Access-Control-Request-Headers, Authorization");
      return;
    }
    httpResponse.addHeader("Access-Control-Allow-Origin", "*");
    chain.doFilter(request, response);
  }

  /**
   * destroy.
   */
  @Override
  public void destroy() {
    //执行完毕之后，清理MDC的traceId
    MDC.clear();
  }
}
