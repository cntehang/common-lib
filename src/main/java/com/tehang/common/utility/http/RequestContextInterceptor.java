package com.tehang.common.utility.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * 设置RestTemplate的请求上线文信息，服务之间，仅传递jwt
 */
public class RequestContextInterceptor implements ClientHttpRequestInterceptor {
  private static final Logger LOG = LoggerFactory.getLogger(RequestContextInterceptor.class);

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {

    HttpHeaders headers = request.getHeaders();
    headers.add(RequestContextInfo.REQUEST_CONTEXT_JWY_KEY, RequestContextInfo.getJwt());
    if (LOG.isTraceEnabled()) {
      LOG.trace("Add jwt to request header:{}", RequestContextInfo.getJwt());
    }

    return execution.execute(request, body);
  }

}