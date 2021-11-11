package com.tehang.common.utility.log;

import brave.Tracer;
import com.google.common.collect.Lists;
import com.tehang.common.utility.DateUtils;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.elasticsearch.ElasticSearchCommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * BizLogAspect的实现基类, 用来辅助实现业务日志记录功能。 启用日志记录的服务必须引入@EnableElasticSearch以实现日志写入。
 */
@Slf4j
public abstract class BizLogAspectBase {

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

  @Autowired
  private Tracer tracer;

  @Autowired
  private ElasticSearchCommonConfig esConfig;

  @Autowired
  private RestHighLevelClient esClient;

  /**
   * 获取当前服务的名称，如domestic-hotel-resource 具体的服务可以实现该方法提供服务名称。
   */
  protected abstract String getSvcName();

  /**
   * 获取当前登陆的用户信息
   * <p>
   * 默认返回null
   */
  protected LoginUserInfo getLoginUserInfo() {
    return null;
  }

  /**
   * 实现类可以调用doAround方法，以实现日志记录。
   */
  protected Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
    log.trace("Enter BizLogAspect.around");

    // 构造日志记录
    BizLogRecord record = createLogRecord(joinPoint);

    long start = System.currentTimeMillis();
    try {
      // 调用目标方法
      BizLogContextHolder.addBizLogContext();
      Object result = joinPoint.proceed();

      // 调用成功
      record.setSuccess(true);
      record.setResult(String.valueOf(result));

      log.trace("Exit BizLogAspect.around, success: true");
      return result;
    }
    catch (Exception ex) {
      // 调用失败
      record.setSuccess(false);
      record.setMsg(ex.getMessage());
      record.setException(ExceptionUtils.getStackTrace(ex));

      log.trace("Exit BizLogAspect.around, success: false");
      throw ex;
    }
    finally {
      // 填写截止时间
      record.setEnd(DateUtils.getCstNow().toString(DATE_TIME_FORMAT));
      record.setElapsed(calculateElapsed(start));

      var currentBizContext = BizLogContextHolder.getCurrentBizLogContext();
      if (currentBizContext != null) {
        record.setBizInfo(currentBizContext.getBizInfo());
      }

      BizLogContextHolder.removeBizLogContext();

      // 保存调用日志
      saveRecord(record);
    }
  }

  private BizLogRecord createLogRecord(ProceedingJoinPoint joinPoint) {
    // 获取方法对象
    var method = ((MethodSignature) joinPoint.getSignature()).getMethod();

    // 获取方法参数值
    Map<String, Object> parameterValues = getNameAndValue(joinPoint);

    // 从BizLog获取tags
    String[] tags = method.getAnnotation(BizLog.class).tags();

    // 构造日志记录
    var record = new BizLogRecord();
    record.setId(UUID.randomUUID().toString());
    record.setSvc(this.getSvcName()); // 服务名通过抽象方法获取，实现类可以提供该方法的实现
    record.setClassName(method.getDeclaringClass().getSimpleName());
    record.setClassFullName(method.getDeclaringClass().getCanonicalName());
    record.setMethod(method.getName());
    record.setTags(Lists.newArrayList(tags));
    record.setUser(getLoginUserInfo());  // 登陆用户信息通过抽象方法获取，实现类可以提供该方法的实现
    record.setParams(JsonUtils.toJson(parameterValues));
    record.setStart(DateUtils.getCstNow().toString(DATE_TIME_FORMAT));
    record.setTraceId(getTraceIdString());
    record.setSpanId(getSpanIdString());
    record.setParentId(getParentIdString());
    return record;
  }

  private float calculateElapsed(long start) {
    long end = System.currentTimeMillis();
    // 结果以秒为单位
    return (end - start) / 1000f;
  }

  private void saveRecord(BizLogRecord record) {
    // 创建es索引请求
    var request = new IndexRequest(esConfig.getIndexNameForBizLog());
    request.id(record.getId());
    request.source(JsonUtils.toJson(record), XContentType.JSON);

    // 异步添加索引
    esClient.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
      @Override
      public void onResponse(IndexResponse indexResponse) {
        // 保存日志成功
        log.trace("save logRecord response: {}", indexResponse.getResult());
      }

      @Override
      public void onFailure(Exception ex) {
        // 保存日志失败
        log.warn("save logRecord failure, msg: {}, record: {}", ex.getMessage(), record, ex);
      }
    });
  }

  /**
   * 获取参数值
   */
  private Map<String, Object> getNameAndValue(ProceedingJoinPoint joinPoint) {
    Map<String, Object> param = new HashMap<>();

    Object[] paramValues = joinPoint.getArgs();
    String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();

    for (int i = 0; i < paramNames.length; i++) {
      param.put(paramNames[i], paramValues[i]);
    }
    return param;
  }

  private String getTraceIdString() {
    String traceId = null;
    if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
      traceId = tracer.currentSpan().context().traceIdString();
    }
    return traceId;
  }

  private String getSpanIdString() {
    String spanId = null;
    if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
      spanId = tracer.currentSpan().context().spanIdString();
    }
    return spanId;
  }

  private String getParentIdString() {
    String parentId = null;
    if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
      parentId = tracer.currentSpan().context().parentIdString();
    }
    return parentId;
  }
}