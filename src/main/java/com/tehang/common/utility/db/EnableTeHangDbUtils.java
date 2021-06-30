package com.tehang.common.utility.db;

import com.tehang.common.utility.db.jpa.OpenJpaSessionAspect;
import com.tehang.common.utility.db.jpa.TransactionHelper;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用特航数据访问相关的辅助类，包括jdbcTemplate, OpenJpaSession, TransactionHelper等.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({CommonJdbcTemplate.class, OpenJpaSessionAspect.class, TransactionHelper.class})
public @interface EnableTeHangDbUtils {

}
