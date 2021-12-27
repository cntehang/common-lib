package com.tehang.common.utility.log.operationlog;

import com.tehang.common.utility.JsonUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户操作时变更的属性信息
 */
@Data
public class ChangedAttribute implements Serializable {

  private static final long serialVersionUID = -1231234537057856201L;
  
  /**
   * 属性名称
   */
  private String attributeName;

  /**
   * 属性名称的可读性表示
   */
  private String attributeAlias;

  /**
   * 属性旧的值，新增操作时为null
   */
  private String oldValue;

  /**
   * 属性新的值
   */
  private String newValue;
  
  // ----------- 方法 ---------------
  
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
