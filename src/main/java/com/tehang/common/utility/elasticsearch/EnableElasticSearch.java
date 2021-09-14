package com.tehang.common.utility.elasticsearch;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用elasticSearch组件. 启用elasticSearch需要配置esHost, esPort, esEnvPrefix三个参数
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ ElasticSearchCommonConfig.class })
public @interface EnableElasticSearch {

}
