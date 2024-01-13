package com.tehang.common.utility.db.page;

/**
 * 分页查询模式：用户发起初始化查询，还是点击下一页，上一页发起的查询？
 */
public enum PageQueryMode {

  /* 初始化查询 */
  Initial,

  /** 下一页查询 */
  Next,

  /** 上一页查询 */
  Previous
}
