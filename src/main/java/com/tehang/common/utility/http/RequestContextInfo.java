package com.tehang.common.utility.http;

import com.tehang.common.utility.token.InnerJwtPayload;

/**
 * 封装当前请求的上下文信息，用于在服务之间传递。
 */
public final class RequestContextInfo {

  private static ThreadLocal<InnerJwtPayload> requestInfo = new ThreadLocal<>();
  private static ThreadLocal<String> jwt = new ThreadLocal<>();
  /**
   * 上下问在header中的key
   */
  public static String REQUEST_CONTEXT_INFO_KEY = "REQUEST_CONTEXT_INFO";

  /**
   * 服务之间请求时，携带在Header中的token
   */
  public static String REQUEST_CONTEXT_JWY_KEY = "REQUEST_CONTEXT_JWY_KEY";

  /**
   * constructor.
   */
  private RequestContextInfo() {
    // do nothing
  }

  public static String getJwt() {
    return jwt.get();
  }

  /**
   * 设置上下文中的jwt
   *
   * @param value
   */
  public static void setJwt(String value) {
    jwt.set(value);
  }

  /**
   * 获取当前的上下文信息
   *
   * @return
   */
  public static InnerJwtPayload getContext() {
    return requestInfo.get();
  }

  /**
   * 设置当前的上下文信息
   *
   * @param info
   */
  public static void setContext(InnerJwtPayload info) {
    requestInfo.set(info);
  }

  public static void clear() {
    requestInfo.remove();
    jwt.get();
  }
}
