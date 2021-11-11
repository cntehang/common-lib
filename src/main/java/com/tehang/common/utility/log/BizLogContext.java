package com.tehang.common.utility.log;

import lombok.Data;

/**
 * 日志中与业务相关的信息上下文
 */
@Data
public class BizLogContext {

  /**
   * 日志中与业务相关的信息
   */
  private BizInfo bizInfo;
}
