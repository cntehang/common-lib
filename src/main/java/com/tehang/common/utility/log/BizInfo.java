package com.tehang.common.utility.log;

import com.tehang.common.utility.JsonUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * 日志中与业务相关的信息
 */
@Data
public class BizInfo implements Serializable {
  
  /**
   * 日期1，格式为yyyy-MM-dd HH:mm:ss.SSS，各业务自定义，比如机票起飞时间，酒店入住日期等。
   */
  private String time1;
  
  /**
   * 日期2，格式为yyyy-MM-dd HH:mm:ss.SSS，各业务自定义，比如酒店离店日期等。
   */
  private String time2;
  
  /**
   * 城市名称
   */
  private String city;
  
  /**
   * 出发地，各业务自定义
   */
  private String from;
  
  /**
   * 到达地，各业务自定义
   */
  private String to;
  
  /**
   * 扩展字段1，各业务自定义
   */
  private String ext1;
  
  /**
   * 扩展字段2，各业务自定义
   */
  private String ext2;
  
  // ----------- 方法 ---------------
  
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
