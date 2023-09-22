package com.tehang.common.utility.event.command;

import com.tehang.common.utility.event.command.commandrecord.CommandRecordJdbcRepository;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用命令模式，命令模式依赖于TransactionalDomainEvent。
 *
 * 启用命令模式的步骤如下：
 *  1. 启用TransactionalDomainEvent并执行相关的步骤；
 *  1. Application中添加: @EnableCommand, 并指定命令事件的消息类型（不同服务的命令消息类型不能相同）。
 *  2. 执行以下sql，创建命令表及命令历史表。
 *
 * create table if not exists `command_record`
 * (
 *   `id`                      varchar(50)  not null    comment 'PK, uuid',
 *   `event_key`               varchar(100) not null    comment '事件key, 此命令对应的事件key，同一个事件key下可以有多个命令，这些命令将按顺序执行。',
 *   `command_type`            varchar(100) not null    comment '命令类型, 执行命令时，根据该类型查找指定的命令bean。',
 *   `command_args`            text         null        comment '命令参数对象的json表示',
 *   `command_return_value`    text         null        comment '命令返回对象的json表示',
 *   `success`                 bit(1)       not null    comment '是否已执行成功',
 *   `seq_no`                  int(11)      not null    comment '命令的顺序号，从1开始',
 *   `create_time`             varchar(23)  not null    comment '创建时间',
 *   `update_time`             varchar(23)  not null    comment '更新时间',
 *   primary key (`id`),
 *   index idx_command_record_event_key(event_key)
 * ) engine = innodb default charset = utf8mb4 comment = '命令记录表';
 *
 * create table if not exists `command_record_his`
 * (
 *   `id`              varchar(50)     not null    comment 'PK, uuid',
 *   `command_id`      varchar(50)     not null    comment '命令id, FK, 引用command_record表',
 *   `success`         bit(1)          not null    comment '执行是否成功',
 *   `error_message`   varchar(300)    null        comment '执行失败时的错误消息',
 *   `create_time`     varchar(23)     not null    comment '创建时间',
 *   primary key (`id`),
 *   index idx_command_record_his_command_id(command_id)
 * ) engine = innodb default charset = utf8mb4 comment = '命令执行历史表';
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
    CommandManager.class,
    CommandEventSubscriber.class,
    CommandLocator.class,
    CommandEventTypeHolderRegistrar.class,
    CommandRecordJdbcRepository.class
})
public @interface EnableCommand {

  /**
   * 配置命令事件的eventType值，此值必须配置，否则会报错。
   */
  String eventType();
}
