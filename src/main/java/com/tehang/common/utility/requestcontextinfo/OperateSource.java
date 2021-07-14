package com.tehang.common.utility.requestcontextinfo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 登录/操作来源设备类型
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OperateSource {

  private static final String WEB = "Web";
  private static final String ADMIN = "Admin";
  private static final String ANDROID = "Android";
  private static final String IOS = "Ios";
  private static final String EXTERNAL = "External"; //外部系统链接跳转过来的
  private static final String WECHAT_H5 = "WechatH5";
  private static final String H5 = "H5";
  private static final String SYSTEM = "System";

  // 未知的操作来源
  private static final String UNKNOWN = "Unknown";

  /**
   * 根据客户端类型获取操作来源
   */
  public static String getOperateSource(String clientType) {
    String operateSource;
    if (StringUtils.equalsIgnoreCase(clientType, "web")) {
      operateSource = WEB;
    }
    else if (StringUtils.equalsIgnoreCase(clientType, "admin")) {
      operateSource = ADMIN;
    }
    else if (StringUtils.equalsIgnoreCase(clientType, "android")) {
      operateSource = ANDROID;
    }
    else if (StringUtils.equalsIgnoreCase(clientType, "ios")) {
      operateSource = IOS;
    }
    else if (StringUtils.equalsIgnoreCase(clientType, OperateSource.WECHAT_H5)) {
      operateSource = WECHAT_H5;
    }
    else if (StringUtils.equalsIgnoreCase(clientType, "external")) {
      operateSource = EXTERNAL;
    }
    else if (StringUtils.equalsIgnoreCase(clientType, "h5")) {
      operateSource = H5;
    }
    else if (StringUtils.equalsIgnoreCase(clientType, "system")) {
      operateSource = SYSTEM;
    }
    else {
      operateSource = UNKNOWN;
    }

    return operateSource;
  }

}
