package com.tehang.common.utility.log.operationlog;

import com.tehang.common.utility.elasticsearch.ElasticSearchCommonConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用用户操作日志的仓储组件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ ElasticSearchCommonConfig.class, UserOperationLogRepository.class })
public @interface EnableUserOperationLogRepository {

}
