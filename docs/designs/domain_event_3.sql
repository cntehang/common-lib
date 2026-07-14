use tmc_services;

-- 第3版: 支持事件数据清理，添加相关的索引，以提高清理的效率

-- 清理消息记录所需要索引
alter table domain_event_consume_record
    add index idx_consume_status_create_time (status, create_time);

alter table command_record
    add index idx_command_create_event (create_time, event_key);

alter table command_record_his
    add index idx_command_his_create_time (create_time);
