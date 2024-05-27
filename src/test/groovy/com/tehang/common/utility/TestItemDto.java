package com.tehang.common.utility;

import com.tehang.common.utility.baseclass.DtoBase;

/**
 * 测试数据dto
 */
public class TestItemDto extends DtoBase {

  public String code;

  public String desc;

  // ---------- 方法 -----------

  public static TestItemDto of(String code, String desc) {
    var item = new TestItemDto();
    item.code = code;
    item.desc = desc;
    return item;
  }
}
