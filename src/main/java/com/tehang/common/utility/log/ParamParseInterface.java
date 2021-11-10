package com.tehang.common.utility.log;

import java.util.Map;

/**
 * 接口参数解析器接口，用于返回日志中与业务相关的信息
 */
public interface ParamParseInterface {

  /**
   * 将接口参数转为日志中与业务相关的信息
   */
  BizInfo getBizInfo(Map<String, Object> parameterValues);
}
