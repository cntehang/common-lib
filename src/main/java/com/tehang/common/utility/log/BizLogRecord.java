package com.tehang.common.utility.log;

import com.tehang.common.utility.JsonUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 表示业务相关的函数调用记录，存在es中，每月新建一个索引，方便查询
 */
@Data
public class BizLogRecord implements Serializable {
  
  /**
   * 日志唯一标识，uuid
   */
  private String id;
  
  /**
   * 服务名，eg: domestic-hotel-service
   */
  private String svc;
  
  /**
   * 记录日志的类名：simpleClassName
   */
  private String className;
  
  /**
   * 记录日志的全类名：classFullName
   */
  private String classFullName;
  
  /**
   * 方法名
   */
  private String method;
  
  /**
   * 日志标签，可以指定多个，用空格分开即可。方便根据标签搜索
   */
  private List<String> tags;
  
  /**
   * 请求参数的json格式
   */
  private String params;
  
  /**
   * 调用结果的json格式
   */
  private String result;
  
  /**
   * 调用是否成功
   */
  private Boolean success;
  
  /**
   * 调用失败时的异常消息
   */
  private String msg;
  
  /**
   * 调用失败时的异常对象的文本表示
   */
  private String exception;
  
  /**
   * 调用时的traceId
   */
  private String traceId;
  
  /**
   * 调用开始时间, 格式为yyyy-MM-dd HH:mm:ss.SSS
   */
  private String start;
  
  /**
   * 调用结束时间, 格式为yyyy-MM-dd HH:mm:ss.SSS
   */
  private String end;
  
  /**
   * 花费时间，以秒为单位
   */
  private float elapsed;
  
  /**
   * 登录的用户相关信息
   */
  private LoginUserInfo user;
  
  /**
   * 业务相关的信息
   */
  private BizInfo bizInfo;
  
  // ----------- 方法 ---------------
  
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
