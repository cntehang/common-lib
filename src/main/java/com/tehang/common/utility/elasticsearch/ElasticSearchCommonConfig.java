package com.tehang.common.utility.elasticsearch;

import com.tehang.common.utility.DateUtils;
import lombok.Getter;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * elasticSearch通用配置，并初始化RestHighLevelClient
 * 启用elasticSearch需要配置esHost, esPort, esEnvPrefix三个参数
 */
@Configuration
@Getter
public class ElasticSearchCommonConfig {

  /**
   * elasticsearch主机名
   */
  @Value("${spring.elasticsearch.host}")
  private String esHost;

  /**
   * elasticsearch端口号
   */
  @Value("${spring.elasticsearch.port}")
  private int esPort;

  /**
   * elasticsearch环境的前缀：由于多个环境需共用一套es的环境，需要加前缀区分
   */
  @Value("${spring.elasticsearch.env-prefix}")
  private String esEnvPrefix;

  /**
   * esClient Bean
   */
  @Bean
  public RestHighLevelClient restHighLevelClient() {
    var builder = RestClient.builder(new HttpHost(esHost, esPort));
    var client = new RestHighLevelClient(builder);
    return client;
  }

  /**
   * 获取【业务日志】索引名称, eg: dev3-log-202109
   */
  public String getIndexNameForBizLog() {
    String thisMonth = DateUtils.getCstNow().toString("yyyyMM");
    return getIndexNameByYearMonth(thisMonth);
  }

  /**
   * 获取【用户操作日志】索引名称, eg: dev1-user-operation-log
   */
  public String getIndexNameForUserOperationLog() {
    return lowerCase(trimToEmpty(esEnvPrefix)) + "user-operation-log";
  }
  
  private String getIndexNameByDateTime(DateTime startDate) {
    String yearMonth = startDate.toString("yyyyMM");
    return getIndexNameByYearMonth(yearMonth);
  }
  
  private String getIndexNameByYearMonth(String yearMonth) {
    return String.format("%slog-%s", lowerCase(trimToEmpty(esEnvPrefix)), yearMonth);
  }
  
  /**
   * 获取【业务日志】索引名称, 每月一个索引，所以可能存在多个。 eg: dev3-log-202109, dev3-log-202110
   */
  public List<String> getIndexNamesForBizLog(String start, String end) {
    // 取开始和截止时间
    var startDate = DateUtils.parseInBeijing(start);
    var endDate = minDate(DateUtils.parseInBeijing(end), DateUtils.nowOfCst());
    
    Set<String> sets = new HashSet<>();
    // 加入开始结束时间对应的索引
    sets.add(getIndexNameByDateTime(startDate));
    sets.add(getIndexNameByDateTime(endDate));
    
    // 添加开始和结束时间中间月份的索引名
    DateTime dt = startDate;
    while (dt.compareTo(endDate) < 0) {
      sets.add(getIndexNameByDateTime(dt));
      dt = dt.plusMonths(1);
    }
    return new ArrayList<>(sets);
  }
  
  private static DateTime minDate(DateTime date1, DateTime date2) {
    return date1.compareTo(date2) < 0 ? date1 : date2;
  }
}
