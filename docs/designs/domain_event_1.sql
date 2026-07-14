use tmc_services;

-- 第1版: 初始化mq相关的事件，命令，命令列表表。不含消费记录表。

-- 领域事件记录表
create table if not exists `domain_event_record`
(
    `id`                 varchar(50)    not null    comment 'PK, uuid',
    `event_key`          varchar(100)   not null    comment '事件key',
    `event_type`         varchar(100)   not null    comment '事件类型，和mq中的tag保持一致(tag可能包含前缀，但eventType字段并不包含前缀)',
    `publisher`          varchar(200)   null        comment '事件发布者(对应于mq中的groupId)',
    `start_deliver_time` varchar(23)    null        comment '设置消息的延时投递时间（绝对时间),最大延迟时间为7天',
    `trace_id`           varchar(200)   null        comment '发布事件所在的TraceId',
    `body`               text           null        comment '事件发送的消息body',
    `status`             varchar(30)    not null    comment '事件的发送状态',
    `publish_time`       varchar(23)    null        comment '事件发布时间，指实际发送到mq的时间',
    `count`              int(11)        not null    comment '实际发送的次数，初始为0',
    `create_time`        varchar(23)    not null    comment '创建时间',
    `update_time`        varchar(23)    not null    comment '更新时间',
    primary key (`id`),
    index idx_domain_event_record_status (status),
    index idx_domain_event_record_status_create_time (status, create_time)
) engine = innodb default charset = utf8mb4 comment = '领域事件记录表';

create table if not exists `command_record`
(
    `id`                      varchar(50)  not null    comment 'PK, uuid',
    `event_key`               varchar(100) not null    comment '事件key, 此命令对应的事件key，同一个事件key下可以有多个命令，这些命令将按顺序执行。',
    `command_type`            varchar(100) not null    comment '命令类型, 执行命令时，根据该类型查找指定的命令bean。',
    `command_args`            text         null        comment '命令参数对象的json表示',
    `command_return_value`    text         null        comment '命令返回对象的json表示',
    `success`                 bit(1)       not null    comment '是否已执行成功',
    `seq_no`                  int(11)      not null    comment '命令的顺序号，从1开始',
    `create_time`             varchar(23)  not null    comment '创建时间',
    `update_time`             varchar(23)  not null    comment '更新时间',
    primary key (`id`),
    index idx_command_record_event_key(event_key)
) engine = innodb default charset = utf8mb4 comment = '命令记录表';

create table if not exists `command_record_his`
(
    `id`              varchar(50)     not null    comment 'PK, uuid',
    `command_id`      varchar(50)     not null    comment '命令id, FK, 引用command_record表',
    `success`         bit(1)          not null    comment '执行是否成功',
    `error_message`   varchar(300)    null        comment '执行失败时的错误消息',
    `create_time`     varchar(23)     not null    comment '创建时间',
    primary key (`id`),
    index idx_command_record_his_command_id(command_id)
) engine = innodb default charset = utf8mb4 comment = '命令执行历史表';
