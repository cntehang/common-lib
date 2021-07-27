package com.tehang.common.utility.http;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用公共rest模板: RestTemplateFactory.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RestTemplateFactory.class, RestTemplateBuilder.class, RestTemplate.class})
public @interface EnableCommonRestTemplate {

}
