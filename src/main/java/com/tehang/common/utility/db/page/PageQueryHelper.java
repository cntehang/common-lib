package com.tehang.common.utility.db.page;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.StringUtils;
import com.tehang.common.utility.db.CommonJdbcTemplate;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 查询分页数据的辅助类（只支持上一页，下一页，不支持总记录数）。
 * <p>
 * 使用要求: 查询结果中需要存在一个唯一的, 不为空的, Long类型的字段，并根据该字段进行排序。
 * <p>
 * 使用方式:
 * - 引用EnableTeHangDbUtils, 以注入PageQueryHelper类；
 * - 查询请求参数需继承至PageRequest类；
 * - 查询返回参数需继承至PageResponse泛型基类；
 * - 调用pageQueryHelper.queryPageResponse进行查询；
 */
@Service
@AllArgsConstructor
@Slf4j
public class PageQueryHelper {

  private CommonJdbcTemplate jdbcTemplate;

  /**
   * 查询分页数据，只支持上一页，下一页，不支持显示总记录数和总页数。
   */
  @SneakyThrows
  public <Record extends IdProvider, Response extends PageResponse<Record>>
    Response queryPageResponse(String sql, Map<String, ?> params,
                               PageRequestInfo pageRequestInfo,
                               String orderByField,
                               OrderByDirection orderByDirection,
                               Class<Response> responseClass,
                               Class<Record> recordClass) {
    // 获取分页大小
    int pageSize = getPageSize(pageRequestInfo.getPageSize());

    // 添加分页的过滤条件
    sql += getPageCondition(pageRequestInfo, orderByField, orderByDirection);

    // 按id排序, 返回记录数加1是为了更准确地计算是否有上一页和下一页。
    sql += String.format(" order by %s %s limit %d ", orderByField, getOrderBySequence(pageRequestInfo.getPageQueryMode(), orderByDirection), pageSize + 1);

    // 查询当前页数据
    log.debug("querySalesRecords sql: {}, request: {}", sql, params);
    List<Record> records = jdbcTemplate.query(sql, params, recordClass);

    // 构造返回结果
    var response = responseClass.getDeclaredConstructor().newInstance();
    response.setPageResultInfo(getPageResultInfo(pageRequestInfo, records, pageSize, orderByDirection));
    response.setRecords(getOrderedRecords(records, pageRequestInfo.getPageQueryMode()));
    return response;
  }

  /** 由于向前翻页时，是反着排序的，这里需要把顺序调整过来 */
  private <Record extends IdProvider> List<Record> getOrderedRecords(List<Record> records, PageQueryMode pageQueryMode) {
    if (pageQueryMode == PageQueryMode.Previous) {
      // 向前翻页, 需要顺序反过来
      Collections.reverse(records);
    }
    return records;
  }

  /** 获取分页的过滤条件：大于或小于当前页的id */
  private static String getPageCondition(PageRequestInfo pageRequestInfo, String orderByField, OrderByDirection orderByDirection) {
    if (orderByDirection == OrderByDirection.Desc) {
      // 倒序
      switch (pageRequestInfo.getPageQueryMode()) {
        case Next:
          // 向后翻页
          if (pageRequestInfo.getMinId() == null) {
            throw new SystemErrorException("分页查询向后翻页时, minId参数不能为空");
          }
          return String.format(" and %s < %d ", orderByField, pageRequestInfo.getMinId());

        case Previous:
          // 往前翻页
          if (pageRequestInfo.getMaxId() == null) {
            throw new SystemErrorException("分页查询向前翻页时, maxId参数不能为空");
          }
          return String.format(" and %s > %d ", orderByField, pageRequestInfo.getMaxId());

        default:
          // 初始化查询时，不需要指定翻页条件
          return StringUtils.EMPTY;
      }
    }
    else {
      // 正序
      switch (pageRequestInfo.getPageQueryMode()) {
        case Next:
          // 向后翻页
          if (pageRequestInfo.getMaxId() == null) {
            throw new SystemErrorException("分页查询向后翻页时, maxId参数不能为空");
          }
          return String.format(" and %s > %d ", orderByField, pageRequestInfo.getMaxId());

        case Previous:
          // 往前翻页
          if (pageRequestInfo.getMinId() == null) {
            throw new SystemErrorException("分页查询向前翻页时, minId参数不能为空");
          }
          return String.format(" and %s < %d ", orderByField, pageRequestInfo.getMinId());

        default:
          // 初始化查询时，不需要指定翻页条件
          return StringUtils.EMPTY;
      }
    }
  }

  /** 根据查询结果数据，计算分页信息 */
  private <Record extends IdProvider> PageResultInfo getPageResultInfo(PageRequestInfo pageRequestInfo, List<Record> result, int pageSize, OrderByDirection orderByDirection) {
    var pageQueryMode = pageRequestInfo.getPageQueryMode();

    if (result.isEmpty()) {
      switch (pageQueryMode) {
        case Initial:
          // 前后都不能翻页
          return PageResultInfo.of(false, false, null, null);
        case Next:
          // 只能往前翻页
          if (orderByDirection == OrderByDirection.Desc) {
            return PageResultInfo.of(true, false, null, pageRequestInfo.getMinId() - 1);
          }
          else {
            return PageResultInfo.of(true, false, pageRequestInfo.getMaxId() + 1, null);
          }
        case Previous:
          // 只能往后翻页
          if (orderByDirection == OrderByDirection.Desc) {
            return PageResultInfo.of(false, true, pageRequestInfo.getMaxId() + 1, null);
          }
          else {
            return PageResultInfo.of(false, true, null, pageRequestInfo.getMinId() - 1);
          }
        default:
          throw new SystemErrorException("invalid pageQueryMode: " + pageQueryMode);
      }
    }
    else if (result.size() > pageSize) {
      // 移除最后一个元素
      result.remove(result.size() - 1);
      var minId = getMinId(result);
      var maxId = getMaxId(result);

      switch (pageQueryMode) {
        case Initial:
          // 只能往后翻页
          return PageResultInfo.of(false, true, minId, maxId);
        case Next:
        case Previous:
          // 既能往前，也能往后翻页
          return PageResultInfo.of(true, true, minId, maxId);
        default:
          throw new SystemErrorException("invalid pageQueryMode: " + pageQueryMode);
      }
    }
    else {
      var minId = getMinId(result);
      var maxId = getMaxId(result);

      switch (pageQueryMode) {
        case Initial:
          // 前后都不能翻页
          return PageResultInfo.of(false, false, minId, maxId);
        case Next:
          // 只能往前翻页
          return PageResultInfo.of(true, false, minId, maxId);
        case Previous:
          // 只能往后翻页
          return PageResultInfo.of(false, true, minId, maxId);
        default:
          throw new SystemErrorException("invalid pageQueryMode: " + pageQueryMode);
      }
    }
  }

  private <Record extends IdProvider> Long getMaxId(List<Record> result) {
    return result.stream()
        .map(IdProvider::resolveId)
        .max(Long::compareTo)
        .orElse(null);
  }

  private <Record extends IdProvider> Long getMinId(List<Record> result) {
    return result.stream()
        .map(IdProvider::resolveId)
        .min(Long::compareTo)
        .orElse(null);
  }

  private static String getOrderBySequence(PageQueryMode pageQueryMode, OrderByDirection orderByDirection) {
    if (orderByDirection == OrderByDirection.Desc) {
      return pageQueryMode == PageQueryMode.Previous ? "asc" : "desc";
    }
    else {
      return pageQueryMode == PageQueryMode.Previous ? "desc" : "asc";
    }
  }

  private static int getPageSize(Integer pageSize) {
    return pageSize == null ? 10 : pageSize;
  }
}
