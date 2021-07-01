package com.tehang.common.infrastructure.filters;

import com.tehang.common.utility.requestcontextinfo.JwtConfig;
import com.tehang.common.utility.requestcontextinfo.RequestContextInfo;
import com.tehang.common.utility.token.InnerJwtPayload;
import com.tehang.common.utility.token.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 解析Jwt的filter
 */
@Component
@Slf4j
@AllArgsConstructor
@Order(Integer.MIN_VALUE + 1)
public class DecodeJwtFilter implements Filter {

  private JwtConfig jwtConfig;

  /**
   * 解析jwt
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    decodeContextInfo(httpRequest);
    chain.doFilter(request, response);
  }

  /**
   * destroy
   */
  @Override
  public void destroy() {
    // 退出时，清除上下文信息
    RequestContextInfo.clear();
  }

  private void decodeContextInfo(HttpServletRequest httpRequest) {
    try {
      String path = httpRequest.getRequestURI();
      String jwt = httpRequest.getHeader(RequestContextInfo.USER_CONTEXT_INFO_KEY);
      log.debug("Path: {}, jwt: {}", path, jwt);

      if (StringUtils.isNotBlank(jwt)) {
        InnerJwtPayload payload = JwtUtils.getPayload(jwt, jwtConfig.getJwtSecret());
        RequestContextInfo.setContext(payload);
        log.debug("Context: {}", RequestContextInfo.getContext());
      }
    } catch (Exception ex) {
      log.warn("Add user context info failed. error message: {}", ex.getMessage());
    }
  }
}
