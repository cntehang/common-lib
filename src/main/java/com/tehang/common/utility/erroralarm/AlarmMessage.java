package com.tehang.common.utility.erroralarm;

import com.tehang.common.utility.baseclass.DtoBase;
import lombok.Getter;
import lombok.Setter;

/**
 * 线上告警消息实体类.
 */
@Getter
@Setter
public class AlarmMessage extends DtoBase {

  /**
   * 方法名.
   */
  private String methodName;

  /**
   * traceId.
   */
  private String traceId;

  /**
   * 服务名称.
   */
  private String serviceName;

  /**
   * 错误消息.
   */
  private String errorMessage;

  /**
   * 异常堆栈文本.
   */
  private String stackText;

  /**
   * 创建时间.
   */
  private String createTime;
}

