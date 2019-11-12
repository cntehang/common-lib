package com.tehang.common.utility.token;

/**
 * 封装当前请求的上下文信息，用于在服务之间传递。
 */
public final class RequestContextInfo {

  private static ThreadLocal<InnerJwtPayload> userInfo = new ThreadLocal<>();
  /**
   * 上下问在header中的key
   */
  public static String REQUEST_CONTEXT_INFO_KEY = "REQUEST_CONTEXT_INFO";

  /**
   * constructor.
   */
  private RequestContextInfo() {
    // do nothing
  }

  /**
   * 获取当前的上下文信息
   *
   * @return
   */
  public static InnerJwtPayload getContext() {
    return userInfo.get();
  }

  /**
   * 设置当前的上下文信息
   *
   * @param info
   */
  public static void setContext(InnerJwtPayload info) {
    userInfo.set(info);
  }

  public static void clear() {
    userInfo.remove();
  }
}
