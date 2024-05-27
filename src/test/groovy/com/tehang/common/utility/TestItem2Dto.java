package com.tehang.common.utility;

import com.tehang.common.utility.baseclass.DtoBase;

/**
 * 测试数据dto
 */
public class TestItem2Dto extends DtoBase {

  public Long code;

  public String desc;

  // ---------- 方法 -----------

  public static TestItem2Dto of(Long code, String desc) {
    var item = new TestItem2Dto();
    item.code = code;
    item.desc = desc;
    return item;
  }
}
