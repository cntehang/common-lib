package com.tehang.common.utility.requestcontextinfo;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.token.InnerJwtPayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RequestContextInfo工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class RequestContextInfoUtils {

  /**
   * 获取当前登录员工的id
   */
  public static Long getCurEmployeeId() {
    return getCurContext().getEmployeeId();
  }

  /**
   * 该请求是否由TMC后台跳转前台发起
   *
   * @return true 若请求来自后台代订人员
   */
  public static boolean isFromAdmin() {
    return getCurContext().isAdmin();
  }

  /**
   * 获取当前登录的客户端类型
   */
  public static String getCurOperateSource() {
    String clientType = getCurContext().getClientType();

    return OperateSource.getOperateSource(clientType);
  }

  /**
   * 获取当前登录后台操作人员的员工id
   */
  public static Long getCurStaffId() {
    return getCurContext().getStaffId();
  }

  /**
   * 获取当前登录后台操作人员的员工姓名
   */
  public static String getCurStaffName() {
    return getCurContext().getStaffName();
  }

  /**
   * 获取上下文
   */
  private static InnerJwtPayload getCurContext() {
    InnerJwtPayload info = RequestContextInfo.getContext();

    if (info == null) {
      log.error("InnerJwtPayload 返回空");
      throw new SystemErrorException("获取请求信息上下文失败");
    }

    return info;
  }
}
