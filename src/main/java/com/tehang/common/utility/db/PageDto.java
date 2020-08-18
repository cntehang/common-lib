package com.tehang.common.utility.db;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页使用的类
 */
@Data
public class PageDto<T> {

  /**
   * 总元素数量
   */
  private long totalElements;

  /**
   * 总页数
   */
  private int totalPage;

  /**
   * 当前第几页，下标从1开始
   */
  private int page;

  /**
   * 当前页多少个元素
   */
  private int size;

  /**
   * 具体的结果
   */
  private List<T> content;

  /**
   * 无参构造方法
   */
  public PageDto() {
    //do nothing
  }

  /**
   * @param content
   * @param page
   */
  public PageDto(List<T> content, Page page) {
    this.content = content;
    this.totalElements = page.getTotalElements();
    this.totalPage = page.getTotalPages();
    this.page = page.getNumber() + 1;
    this.size = page.getNumberOfElements();
  }

  /**
   * 创建空的分页对象，通常用于接口定义阶段
   */
  public static <T> PageDto<T> createEmptyPageDto() {
    List<T> emptyContent = new ArrayList<>();
    Page emptyPage = new PageImpl<>(emptyContent);
    return new PageDto<>(emptyContent, emptyPage);
  }

  /**
   * @param content
   * @param page
   * @return
   */
  public static <Z> PageDto<Z> build(List<Z> content, Page page) {
    return new PageDto<>(content, page);
  }

}
