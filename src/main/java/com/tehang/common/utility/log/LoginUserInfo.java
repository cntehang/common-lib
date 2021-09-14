package com.tehang.common.utility.log;

import com.tehang.common.utility.JsonUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户相关的信息
 */
@Data
public class LoginUserInfo implements Serializable {
  
  /**
   * 前台的员工id
   */
  private String employeeId;
  
  /**
   * 前台的员工姓名
   */
  private String employeeName;
  
  /**
   * 前台的公司id
   */
  private String corpId;
  
  /**
   * 前台的公司类型
   */
  private String corpType;
  
  /**
   * 是否后台人员跳转前台？
   */
  private Boolean admin;
  
  /**
   * 后台人员id
   */
  private String staffId;
  
  /**
   * 后台人员姓名
   */
  private String staffName;
  
  /**
   * 操作来源
   */
  private String operateSource;
  
  // ----------- 方法 ---------------
  
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
