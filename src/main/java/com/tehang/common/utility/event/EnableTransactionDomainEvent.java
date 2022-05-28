package com.tehang.common.utility.event;

import com.tehang.common.utility.ApplicationContextProvider;
import com.tehang.common.utility.event.mq.BroadcastingMqConsumer;
import com.tehang.common.utility.event.mq.ClusteringMqConsumer;
import com.tehang.common.utility.event.mq.LocalTransactionCheckerService;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.mq.MqProducer;
import com.tehang.common.utility.event.publish.EventPublisher;
import com.tehang.common.utility.event.publish.TransactionEventPublisher;
import com.tehang.common.utility.lock.DistributedLockFactory;
import com.tehang.common.utility.redis.CommonRedisOperator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用事务性的事件发布组件。在发布事件时使用TransactionalEventPublisher发布事务性事件。
 *
 * 启用事物性的事件发布组件的步骤如下：
 * 1. Application中添加: @EnableTransactionDomainEvent
 * 2. Application中添加: @EnableJpaRepositories(basePackageClasses = { ThStringUtils.class, Application.class }, repositoryBaseClass = ExtendedJpaRepository.class)
 * 3. Application中添加: @EntityScan(basePackageClasses = { ThStringUtils.class, Application.class })
 * 4. 执行以下sql，创建事件存储表。
 *
 * -- 领域事件记录表
 * create table if not exists `domain_event_record`
 * (
 *   `id`                 varchar(50)  not null    comment 'PK, uuid',
 *   `event_key`          varchar(100) not null    comment '事件key',
 *   `event_type`         varchar(100) not null    comment '事件类型，和mq中的tag保持一致(tag可能包含前缀，但eventType字段并不包含前缀)',
 *   `publisher`          varchar(200) null        comment '事件发布者(对应于mq中的groupId)',
 *   `start_deliver_time` varchar(23)  null        comment '设置消息的延时投递时间（绝对时间),最大延迟时间为7天',
 *   `trace_id`           varchar(200) null        comment '发布事件所在的TraceId',
 *   `body`               text         null        comment '事件发送的消息body',
 *   `publish_time`       varchar(23)  null        comment '事件发布时间，指实际发送到mq的时间',
 *   `create_time`        varchar(23)  not null    comment '创建时间',
 *   `update_time`        varchar(23)  not null    comment '更新时间',
 *   primary key (`id`),
 *   index idx_domain_event_record_status(status),
 *   index idx_domain_event_record_status_create_time(status, create_time)
 * ) engine = innodb default charset = utf8mb4 comment = '领域事件记录表';
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({MqConfig.class,
  MqProducer.class,
  EventPublisher.class,
  ClusteringMqConsumer.class,
  BroadcastingMqConsumer.class,
  ApplicationContextProvider.class,
  DistributedLockFactory.class,
  CommonRedisOperator.class,
  TransactionEventPublisher.class,
  LocalTransactionCheckerService.class
})
public @interface EnableTransactionDomainEvent {

}
