package com.tehang.common.utility.db.page;

/**
 * 表示一个具有id值的对象，用在分页查询中，根据该id进行排序。
 */
public interface IdProvider {

  /** 获取对象的id值。*/
  long resolveId();
}
