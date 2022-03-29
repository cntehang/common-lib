package com.tehang.common.utility.external;

import com.tehang.common.utility.http.RestTemplateFactory;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用外部服务代理类: EnableExternalServiceProxy.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ExternalServiceProxy.class, RestTemplateFactory.class})
public @interface EnableExternalServiceProxy {

}
