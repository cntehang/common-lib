package com.tehang.common.infrastructure.filters;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tehang.common.utility.api.front.DataContainer;
import com.tehang.common.utility.consts.ExceptionHandlerConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 目前所有接口的返回头采用了封装型，这统一做封装
 */
@Component
@Order
@SuppressWarnings("PMD.SimplifyStartsWith")
public class ResponseContainerFilter implements Filter {

  /**
   * LOGGER.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ResponseContainerFilter.class);

  /**
   * 为所有 Controller 返回的数据同一添加 DateContainer 层
   * 如果是 CommonExceptionHandler/ControllerExceptionHandler 来到这里
   *
   * @param request  HTTP 请求
   * @param response HTTP 响应
   * @param chain    Filter 调用链
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    String requestUri = ((HttpServletRequest) request).getRequestURI();
    if (isInWhiteList(requestUri)) {
      LOG.trace("Go through response container filter in white list");

      chain.doFilter(request, response);

    } else {

      HttpServletResponse httpResponse = (HttpServletResponse) response;
      ResponseWrapper mr = new ResponseWrapper(httpResponse);
      LOG.trace("Go through response container with response wrapped");

      chain.doFilter(request, mr);

      String plainResponse = new String(mr.getDataStream(), StandardCharsets.UTF_8);
      String responseContainerContainedResponse;

      if (isFromExceptionHandler(httpResponse)) {
        LOG.trace("Get wrapped response from exception handler, no need to warp again");
        responseContainerContainedResponse = plainResponse;
      } else {
        LOG.trace("Get plain response from controller, warp with response container");
        responseContainerContainedResponse = warpWithResponseContainer(plainResponse);
      }

      httpResponse.setHeader("Content-type", "application/json;charset=UTF-8");
      //Content-Length代表字节数，而非字符数，如果直接用value字符串的长度赋值，在有中文的情况下会导致前台接受的json字符串长度不够而无法正确解析
      httpResponse.setHeader("Content-length", String.valueOf(responseContainerContainedResponse.getBytes(StandardCharsets.UTF_8).length));
      response.getWriter().println(responseContainerContainedResponse);
    }

  }

  private boolean isInWhiteList(String requestUri) {
    return requestUri.contains("getValidateCode") || requestUri.contains("swagger") || requestUri.contains("api-docs")
        || requestUri.contains("download") || requestUri.contains("export")
        || requestUri.contains("importFrequentTravellerForSK")
        || requestUri.contains("importCustomerForSK");
  }

  private boolean isFromExceptionHandler(HttpServletResponse httpResponse) {
    String errorMsg = httpResponse.getHeader(ExceptionHandlerConstants.RESPONSE_HEADER_ERROR_MESSAGE);
    return StringUtils.isNotBlank(errorMsg);
  }

  private String warpWithResponseContainer(String plainResponse) throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    Object data = getData(plainResponse, mapper);
    DataContainer dataContainer = new DataContainer(data);

    LOG.trace("Add data container: {}", dataContainer);
    return mapper.writeValueAsString(dataContainer);
  }

  private Object getData(String plainResponse, ObjectMapper mapper) throws IOException {
    Object data;
    if (mayBeJson(plainResponse)) {
      LOG.trace("Response maybe json, try to parse");
      data = parseJson(plainResponse, mapper);
    } else {
      LOG.trace("Response is not json, return directly");
      // 用于处理两种情况：
      // 1. 我们不想让 Jackson 处理的情形。如返回一个 String 类型的纯数字字符，Jackson 会将其转化为 Number 返回
      // 2. Jackson 无法处理的情形。如 "123aaa" 这种字符串
      data = plainResponse;
    }
    return data;
  }

  private Object parseJson(String plainResponse, ObjectMapper mapper) throws IOException {
    Object data;
    try {
      data = mapper.readValue(plainResponse, Object.class);
      LOG.trace("Parse json successfully");
    } catch (JsonParseException | JsonMappingException ex) {
      // 我们对字符串是否为 Json 字符串的判断很简单，还有很多种不能转换的情形，如"{hello"这种情形的字符串
      LOG.trace("Parse json failed, return string directly");
      data = plainResponse;
    }
    return data;
  }

  private boolean mayBeJson(String value) {
    return StringUtils.isNotBlank(value) && StringUtils.startsWith(value, "{") ||
        StringUtils.isNotBlank(value) && StringUtils.startsWith(value, "[");
  }

  /**
   * destroy.
   */
  @Override
  public void destroy() {
    //执行完毕之后，清理MDC的traceId
  }

}
