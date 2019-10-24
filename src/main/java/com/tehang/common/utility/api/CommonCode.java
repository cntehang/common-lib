package com.tehang.common.utility.api;

/**
 * 通用型数据返回,
 * 全局统一
 */
public class CommonCode {
  /**
   * 成功的code，全局统一
   */
  public static final int SUCCESS_CODE = 0;
  /**
   * 成功的message，全局统一
   */
  public static final String SUCCESS_MESSAGE = "OK";

  /**
   * 参数错误
   */
  public static final int PARAMETER_ERROR_CODE = 98;
  /**
   * 参数错误信息，默认信息.
   */
  public static final String PARAMETER_ERROR_MESSAGE = "参数错误";

  /**
   * 其他异常
   */
  public static final int COMMON_ERROR_CODE = 99;
  /**
   * 参数错误
   */
  public static final int UNHANDLER_ERROR_CODE = 500;
  /**
   * 参数错误信息，默认信息.
   */
  public static final String UNHANDLER_ERROR_MESSAGE = "未知错误";

}
