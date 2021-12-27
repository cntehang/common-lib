package com.tehang.common.utility.log.operationlog;

import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.elasticsearch.ElasticSearchCommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户操作日志仓储类，用来保存UserOperationLog到elasticSearch.
 */
@Component
@Slf4j
public class UserOperationLogRepository {

  @Autowired
  private ElasticSearchCommonConfig esConfig;

  @Autowired
  private RestHighLevelClient esClient;

  /**
   * 保存用户操作日志到elasticSearch，采用异步保存。
   */
  public void saveOperationLog(UserOperationLog userOperationLog) {
    // 创建es索引请求
    var request = new IndexRequest(esConfig.getIndexNameForUserOperationLog());
    request.id(userOperationLog.getId());
    request.source(JsonUtils.toJson(userOperationLog), XContentType.JSON);

    // 异步添加索引
    esClient.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
      @Override
      public void onResponse(IndexResponse indexResponse) {
        // 保存日志成功
        log.trace("save userOperationLog response: {}", indexResponse.getResult());
      }

      @Override
      public void onFailure(Exception ex) {
        // 保存日志失败
        log.warn("save userOperationLog failure, msg: {}, userOperationLog: {}", ex.getMessage(), userOperationLog, ex);
      }
    });
  }
}