package com.tehang.common.infrastructure.exceptions;

/**
 * 表示业务方面的报警信息，并非系统错误，此异常将通过代码80返回给前端。
 */
public class BizWarningException extends ApplicationException {

  /** 无参构造器 */
  public BizWarningException() {
    super(80);
    // do nothing.
  }

  public BizWarningException(String message) {
    super(80, message);
  }
}
