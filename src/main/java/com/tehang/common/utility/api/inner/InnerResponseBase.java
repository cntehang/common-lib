package com.tehang.common.utility.api.inner;

import com.tehang.common.utility.baseclass.DtoBase;
import lombok.Getter;
import lombok.Setter;

/**
 * 服务间调用返回结果的基类，包含code, message字段。
 */
@Getter
@Setter
public abstract class InnerResponseBase extends DtoBase {

  // 接口调用成功的代码
  public static final int SUCCESS_CODE = 0;

  // 接口调用失败的状态
  public static final int FAILED_CODE = 1;

  private int code;
  private String message;

  /* 默认的无参构造 */
  public InnerResponseBase() {
    // default constructor
  }

  public InnerResponseBase(int code, String message) {
    this.code = code;
    this.message = message;
  }

  /** 调用是否成功? */
  public boolean successful() {
    return code == SUCCESS_CODE;
  }
}
