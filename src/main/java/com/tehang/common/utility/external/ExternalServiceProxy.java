package com.tehang.common.utility.external;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.api.inner.EmptyResponseContainer;
import com.tehang.common.utility.api.inner.ResponseContainer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 调用外部服务的接口封装类
 */
@Service
@Slf4j
@AllArgsConstructor
public class ExternalServiceProxy {

  private final RestTemplate restTemplate;

  /**
   * 调用外部服务，使用POST请求
   */
  public <Req, Resp> Resp post(String url, Req request, ParameterizedTypeReference<Resp> responseType) {
    log.debug("Enter ExternalServiceProxy.post, request: {}", request);

    ResponseEntity<Resp> responseEntity =
      restTemplate.exchange(url, HttpMethod.POST,
                            new HttpEntity<>(request),
                            responseType);

    Resp resp = responseEntity.getBody();

    if (resp == null) {
      log.debug("ExternalServiceProxy.post return null, request: {}", request);
      throw new SystemErrorException("调用外部接口返回为空");
    }

    if (resp instanceof ResponseContainer) {
      var responseContainer = (ResponseContainer) resp;
      if (responseContainer.getCode() == 0) {
        log.debug("Exit ExternalServiceProxy.post");
        log.trace("Exit ExternalServiceProxy.post, success, response: {}", responseContainer);

      } else {
        log.debug("Exit ExternalServiceProxy.post, failed, url: {}, request: {}, response: {}", url, request, responseContainer);
        throw new SystemErrorException(responseContainer.getMessage());
      }
    } else if (resp instanceof EmptyResponseContainer) {
      var responseContainer = (EmptyResponseContainer) resp;
      if (responseContainer.getCode() == 0) {
        log.debug("Exit ExternalServiceProxy.post");
        log.trace("Exit ExternalServiceProxy.post, success, response: {}", responseContainer);

      } else {
        log.debug("Exit ExternalServiceProxy.post, failed, url: {}, request: {}, response: {}", url, request, responseContainer);
        throw new SystemErrorException(responseContainer.getMessage());
      }
    }

    return resp;
  }
}
