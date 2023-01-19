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
}
