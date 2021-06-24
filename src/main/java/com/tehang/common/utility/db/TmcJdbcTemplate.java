package com.tehang.common.utility.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@AllArgsConstructor
public class TmcJdbcTemplate {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final JdbcTemplate plainJdbcTemplate;

  /**
   * 列表查询
   *
   * @param sql
   * @param mappedClass
   * @param <T>
   * @return
   */
  public <T> List<T> query(String sql, Class<T> mappedClass) {
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(mappedClass));
  }

  /**
   * 列表查询，动态参数
   *
   * @param sql         sql语句
   * @param argObj      参数
   * @param mappedClass 结果映射类
   * @param <T>         结果映射类型
   * @return 查询结果
   */
  public <T> List<T> query(String sql, Object argObj, Class<T> mappedClass) {
    BeanPropertySqlParameterSource parameter = new BeanPropertySqlParameterSource(argObj);

    return jdbcTemplate.query(sql, parameter, new BeanPropertyRowMapper<>(mappedClass));
  }

  /**
   * 列表查询，动态参数
   *
   * @param sql         sql语句
   * @param paramMap    参数map
   * @param mappedClass 结果映射类
   * @param <T>         结果映射类型
   * @return 查询结果
   */
  public <T> List<T> query(String sql, Map<String, ?> paramMap, Class<T> mappedClass) {
    return jdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<>(mappedClass));
  }

  /**
   * 列表查询，供结果是基础类型或者String使用
   *
   * @param sql         sql语句
   * @param paramMap    参数map
   * @param elementType 结果映射类（可用于基本数据类型的包装类）
   * @param <T>         结果映射类型
   * @return 查询结果
   */
  public <T> List<T> queryListForType(String sql, Map<String, ?> paramMap, Class<T> elementType) {
    return jdbcTemplate.queryForList(sql, paramMap, elementType);
  }

  /**
   * 执行查询，返回字符串结果列表
   *
   * @param sql      需要执行的 SQL
   * @param paramMap 参数
   * @return 结果列表
   */
  public List<String> queryForList(String sql, Map<String, ?> paramMap) {
    return jdbcTemplate.queryForList(sql, paramMap, String.class);
  }

  /**
   * 查询一个字段
   *
   * @param sql 查询 sql
   * @return 查询字段结果
   */
  public String queryForColumn(String sql) {
    return queryForColumn(sql, new ConcurrentHashMap<>());
  }

  /**
   * 查询一个字段
   *
   * @param sql      查询 sql
   * @param paramMap 查询参数
   * @return 查询字段结果
   */
  public String queryForColumn(String sql, Map<String, ?> paramMap) {

    String result = null;
    List<String> strLst = jdbcTemplate.query(sql, paramMap, (rs, rowNum) -> rs.getString(1));

    if (CollectionUtils.isNotEmpty(strLst)) {
      result = strLst.get(0);
    }

    return result;
  }

  /**
   * 查询一个字段
   *
   * @param sql       查询 sql
   * @param paramMap  查询参数
   * @param rowMapper 行数据映射
   * @return 查询字段结果
   */
  public <T> T queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) {
    return jdbcTemplate.queryForObject(sql, paramMap, rowMapper);
  }

  /**
   * 查询一个字段，布尔类型
   *
   * @param sql 查询 sql
   * @return 查询字段结果
   */
  public Boolean queryForBooleanColumn(String sql) {

    return jdbcTemplate.queryForObject(sql, new ConcurrentHashMap<>(), Boolean.class);
  }

  /**
   * 查询一个字段，布尔类型
   *
   * @param sql      查询 sql
   * @param paramMap 查询参数
   * @return 查询字段结果
   */
  public Boolean queryForBooleanColumn(String sql, Map<String, ?> paramMap) {

    return jdbcTemplate.queryForObject(sql, paramMap, Boolean.class);
  }

  /**
   * 查询一个字段
   *
   * @param sql      查询 sql
   * @param paramMap 查询参数
   * @return 查询字段结果
   */
  public <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> mappedClass) {
    return jdbcTemplate.queryForObject(sql, paramMap, mappedClass);
  }

  /**
   * 分页查询
   *
   * @param sql         sql语句
   * @param baseParams  参数（包括分页、排序等）
   * @param mappedClass 结果映射类
   * @param <T>         结果映射类型
   * @return 分页查询结果
   */
  public <T> PageDto<T> pagedQuery(String sql, PageSearchBaseDto baseParams, Class<T> mappedClass) {

    BeanPropertySqlParameterSource parameter = new BeanPropertySqlParameterSource(baseParams);

    // 统计总数
    Long total = this.countTotal(sql, parameter);

    // 执行查询
    PageRequest pageRequest = PageUtil.buildPageRequest(baseParams.getPageNumber(), baseParams.getPageSize());
    String pagedSql = SqlUtils.buildPagedSql(sql, pageRequest);
    List<T> content = jdbcTemplate.query(pagedSql, parameter, new BeanPropertyRowMapper<>(mappedClass));

    // 构建分页结果
    return PageUtil.buildPageResponse(content, pageRequest, total);
  }

  public <T> PageDto<T> pagedQueryWithoutRefactor(String searchSql, PageSearchBaseDto baseParams, Class<T> mappedClass) {

    BeanPropertySqlParameterSource parameter = new BeanPropertySqlParameterSource(baseParams);

    // 统计总数
    Long total = this.countTotalWithoutRefactor(searchSql, parameter);

    // 执行查询
    PageRequest pageRequest = PageUtil.buildPageRequest(baseParams.getPageNumber(), baseParams.getPageSize());
    String pagedSql = SqlUtils.buildPagedSql(searchSql, pageRequest);
    List<T> content = jdbcTemplate.query(pagedSql, parameter, new BeanPropertyRowMapper<>(mappedClass));

    // 构建分页结果
    return PageUtil.buildPageResponse(content, pageRequest, total);
  }

  /**
   * 分页查询
   *
   * @param sql           sql语句
   * @param paramMap      参数map
   * @param mappedClass   结果映射类
   * @param searchBaseDto 分页参数
   * @param <T>           结果映射类型
   * @return 分页查询结果
   */
  public <T> PageDto<T> pagedQuery(String sql, Map<String, ?> paramMap, Class<T> mappedClass, PageSearchBaseDto searchBaseDto) {
    Long count = jdbcTemplate.queryForObject(SqlUtils.buildCountSql(sql), paramMap, Long.class);

    PageRequest pageRequest = PageUtil.buildPageRequest(searchBaseDto.getPageNumber(), searchBaseDto.getPageSize());
    List<T> content = jdbcTemplate.query(SqlUtils.buildPagedSql(sql, pageRequest), paramMap, new BeanPropertyRowMapper<>(mappedClass));

    return PageUtil.buildPageResponse(content, pageRequest, count);
  }

  /**
   * 分页查询
   *
   * @param sql           sql语句
   * @param paramMap      参数map
   * @param mappedClass   结果映射类
   * @param searchBaseDto 分页参数
   * @param <T>           结果映射类型
   * @return 分页查询结果
   */
  public <T> PageDto<T> pagedQueryBySubquery(String sql, Map<String, ?> paramMap, Class<T> mappedClass, PageSearchBaseDto searchBaseDto) {
    String countSql = "SELECT COUNT(*) FROM (" + sql + ") anony_counter";
    Long count = jdbcTemplate.queryForObject(countSql, paramMap, Long.class);

    PageRequest pageRequest = PageUtil.buildPageRequest(searchBaseDto.getPageNumber(), searchBaseDto.getPageSize());
    List<T> content = jdbcTemplate.query(SqlUtils.buildPagedSql(sql, pageRequest), paramMap, new BeanPropertyRowMapper<>(mappedClass));

    return PageUtil.buildPageResponse(content, pageRequest, count);
  }

  private Long countTotal(String sql, BeanPropertySqlParameterSource parameter) {
    String countSql = SqlUtils.buildCountSql(sql);
    String total = jdbcTemplate.queryForObject(countSql, parameter, String.class);

    if (StringUtils.isBlank(total)) {
      log.warn("执行count语句返回: 【{}】，sql: {}", total, sql);
      total = "0";
    }
    return Long.parseLong(total);
  }

  private Long countTotalWithoutRefactor(String sql, BeanPropertySqlParameterSource parameter) {
    String countSql = SqlUtils.buildCountSqlWithoutRefactor(sql);
    String total = jdbcTemplate.queryForObject(countSql, parameter, String.class);

    if (StringUtils.isBlank(total)) {
      log.warn("执行count语句返回: 【{}】，sql: {}", total, sql);
      total = "0";
    }
    return Long.parseLong(total);
  }


  /**
   * 批量执行数据库更新/插入语句
   *
   * @param sql    sql语句
   * @param setter 参数列表
   */
  public void batchUpdate(String sql, BatchPreparedStatementSetter setter) {
    plainJdbcTemplate.batchUpdate(sql, setter);
  }

  /**
   * 执行更新状态的sql语句
   *
   * @param sql      sql语句
   * @param paramMap 参数map
   * @return 更新成功条目数
   * @throws DataAccessException
   */
  public int update(String sql, Map<String, ?> paramMap) throws DataAccessException {
    return jdbcTemplate.update(sql, paramMap);
  }

  /**
   * 批量执行更新状态的sql语句
   *
   * @param sql            sql语句
   * @param batchParamMaps 参数map
   * @return 更新成功条目数
   */
  public int[] batchUpdate(String sql, Map<String, ?>[] batchParamMaps) {
    return jdbcTemplate.batchUpdate(sql, batchParamMaps);
  }
}
