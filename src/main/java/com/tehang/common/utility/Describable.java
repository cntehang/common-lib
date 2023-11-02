package com.tehang.common.utility;

/**
 * 定义一个接口，包含一个返回description的方法。
 * 一般用于定义枚举类型时，返回一个描述信息。
 */
public interface Describable {

  /**
   * 获取描述信息。
   */
  String getDescription();
}
