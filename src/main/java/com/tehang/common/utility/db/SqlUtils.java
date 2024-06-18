package com.tehang.common.utility.db;

import org.springframework.data.domain.Pageable;

import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * sql语句处理工具类.
 */
public final class SqlUtils {

  private SqlUtils() {
    // do nothing
  }

  /**
   * 构建分页sql.
   *
   * @param sql       原始sql
   * @param pageParam 分页参数
   * @return 分页sql
   */
  public static String buildPagedSql(String sql, Pageable pageParam) {
    return sql + " limit " + pageParam.getOffset() + ", " + pageParam.getPageSize();
  }

  /**
   * 构建count sql，不做order by 的优化.
   */
  public static String buildCountSqlWithoutRefactor(String sql) {
    return "SELECT COUNT(*) " + removeSelect(sql);
  }

  /**
   * 去除SELECT *语句，便于SELECT count(*).
   */
  private static String removeSelect(String sql) {
    int beginPosition = sql.toLowerCase(Locale.US).indexOf("from ");
    return sql.substring(beginPosition);
  }

  /**
   * 获取排序方向，默认逆序.
   */
  public static String getOrderByDirection(String orderByDirection) {
    String result = "desc";
    if (equalsIgnoreCase(orderByDirection, "asc")) {
      result = "asc";
    }
    return result;
  }
}
