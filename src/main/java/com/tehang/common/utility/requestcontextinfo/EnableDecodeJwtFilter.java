package com.tehang.common.utility.requestcontextinfo;

import com.tehang.common.infrastructure.filters.DecodeJwtFilter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用DecodeJwtFilter组件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({JwtConfig.class, DecodeJwtFilter.class})
public @interface EnableDecodeJwtFilter {
}
