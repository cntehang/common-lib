package com.tehang.common.utility.requestcontextinfo;

import com.tehang.common.utility.token.InnerJwtPayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 当前请求的上下文信息
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestContextInfo {

  private static ThreadLocal<InnerJwtPayload> userInfo = new ThreadLocal<>();
  public static final String USER_CONTEXT_INFO_KEY = "user_context_info_key";

  /**
   * 获取当前的上下文信息
   */
  public static InnerJwtPayload getContext() {
    return userInfo.get();
  }

  /**
   * 设置当前的上下文信息
   */
  public static void setContext(InnerJwtPayload info) {
    userInfo.set(info);
  }

  /**
   * 清除上下文信息
   */
  public static void clear() {
    userInfo.remove();
  }
}
