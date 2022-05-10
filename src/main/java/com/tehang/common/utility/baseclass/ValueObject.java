package com.tehang.common.utility.baseclass;

import com.tehang.common.utility.JsonUtils;

import java.io.Serializable;

/**
 * 值对象基类
 */
public abstract class ValueObject implements Serializable {

  private static final long serialVersionUID = -5554308939380869754L;

  // -------------- toString -------------
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
