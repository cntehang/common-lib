package com.tehang.common.utility.db;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * 分页相关工具类.
 */
public final class PageUtil {

  private PageUtil() {
    // do nothing
  }

  public static PageRequest build(PageSearchBaseDto searchBaseDto) {
    return PageRequest.of(searchBaseDto.getPageNumber(), searchBaseDto.getPageSize());
  }

  /**
   * 构建分页和排序的请求.
   */
  public static PageRequest buildPageRequest(int pageNum, int pageSize) {
    return PageRequest.of(pageNum - 1, pageSize);
  }

  /**
   * 构建分页和排序的请求.
   */
  public static PageRequest buildPageRequest(int pageNum, int pageSize, Sort sort) {
    return PageRequest.of(pageNum - 1, pageSize, sort);
  }

  /**
   * 构建分页查询结果.
   *
   * @param content     数据内容
   * @param pageRequest 分页请求参数
   * @param total       数据总数
   * @return 分页查询结果
   */
  public static <T> PageDto<T> buildPageResponse(List<T> content, PageRequest pageRequest, Long total) {
    Page<T> page = new PageImpl<>(content, pageRequest, Optional.ofNullable(total).orElseThrow(() -> new SystemErrorException("没有查询到总条数")));

    return PageDto.build(page.getContent(), page);
  }

}
