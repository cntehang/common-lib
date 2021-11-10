package com.tehang.common.utility.log;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 查找满足条件的参数解析器
 */
@Service
@AllArgsConstructor
public class ParamParseFinder {

  private final ApplicationContext applicationContext;

  /**
   * 查找指定的参数解析器实现
   */
  public ParamParseInterface findParamParse(String serviceClass) {
    return applicationContext.getBeansOfType(ParamParseInterface.class)
             .values()
             .stream()
             .filter(item -> StringUtils.equalsIgnoreCase(item.getClass().getSimpleName(), serviceClass))
             .findFirst()
             .orElseThrow(() -> new SystemErrorException("can not found service instance, serviceName: " + serviceClass));
  }
}
