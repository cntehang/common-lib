package com.tehang.common.utility.api.inner;

/**
 * 空的返回结果类：只包含code, message字段。
 */
public class EmptyInnerResponse extends InnerResponseBase {

  /** 默认的无参构造 */
  public EmptyInnerResponse() {
    this(SUCCESS_CODE, null);
  }

  public EmptyInnerResponse(int code, String message) {
    super(code, message);
  }

  // ----------- 其他方法 ----------

  /** 创建一个调用成功的的空返回对象。*/
  public static EmptyInnerResponse successOf() {
    return new EmptyInnerResponse(SUCCESS_CODE, null);
  }

  /** 创建一个调用失败的空返回对象。*/
  public static EmptyInnerResponse failedOf(String errorMsg) {
    return new EmptyInnerResponse(FAILED_CODE, errorMsg);
  }
}
