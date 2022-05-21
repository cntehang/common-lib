package com.tehang.common.utility.event;

import com.tehang.common.utility.ApplicationContextProvider;
import com.tehang.common.utility.event.mq.BroadcastingMqConsumer;
import com.tehang.common.utility.event.mq.ClusteringMqConsumer;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.mq.MqProducer;
import com.tehang.common.utility.event.publish.EventPublisher;
import com.tehang.common.utility.event.publish.SendDomainEventRecordsToMqService;
import com.tehang.common.utility.event.publish.TransactionalEventPublisher;
import com.tehang.common.utility.event.publish.eventrecord.DomainEventRecordRepository;
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
 * 启用事件发布组件.
 * 如果使用TransactionalEventPublisher，需要在服务中添加一个定时任务，该定时任务调用SendDomainEventRecordsToMqService以发布事件到mq（建议每秒调用一次）。
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
  TransactionalEventPublisher.class,
  SendDomainEventRecordsToMqService.class,
  DomainEventRecordRepository.class
})
public @interface EnableDomainEvent {

}
