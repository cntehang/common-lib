package com.tehang.common.utility.notification.wecom;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用企业微信群消息发送组件.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({WeComMessageSender.class, WeComMessageSenderRestTemplateFactory.class})
public @interface EnableWeComMessageSender {

}
