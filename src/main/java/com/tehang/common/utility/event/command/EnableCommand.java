package com.tehang.common.utility.event.command;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用命令模式，命令模式依赖于TransactionalDomainEvent。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
    CommandManager.class,
    CommandEventSubscriber.class,
    CommandLocator.class,
    CommandEventTypeHolderRegistrar.class
})
public @interface EnableCommand {

  /**
   * 配置命令事件的eventType值，此值必须配置，否则会报错。
   */
  String eventType();
}
