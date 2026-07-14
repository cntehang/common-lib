use tmc_services;

-- 第2版: 事件防重，以及消费幂等处理。添加事件表唯一索引，以及消费记录表

-- 添加事件key唯一索引
alter table domain_event_record
    add unique key uk_domain_event_record_type_key (event_type, event_key);

-- 添加事件消费记录表
create table if not exists `domain_event_consume_record`
(
    `id`             varchar(50)  not null comment 'PK, uuid',
    `event_key`      varchar(100) not null comment '事件key',
    `event_type`     varchar(100) not null comment '事件类型',
    `subscriber_id`  varchar(200) not null comment '订阅者稳定标识',
    `status`         varchar(30)  not null comment '消费状态',
    `consume_time`   varchar(23)  null     comment '消费成功时间',
    `error_message`  varchar(500) null     comment '错误信息，预留字段',
    `create_time`    varchar(23)  not null comment '创建时间',
    `update_time`    varchar(23)  not null comment '更新时间',
    primary key (`id`),
    unique key `uk_event_consume` (`event_key`, `event_type`, `subscriber_id`),
    key `idx_domain_event_consume_record_event` (`event_type`, `event_key`),
    key `idx_domain_event_consume_record_subscriber` (`subscriber_id`)
) engine = innodb default charset = utf8mb4 comment = '领域事件消费记录表';
