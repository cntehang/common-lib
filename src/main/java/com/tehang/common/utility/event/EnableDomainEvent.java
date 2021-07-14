package com.tehang.common.utility.event;

import com.tehang.common.utility.ApplicationContextProvider;
import com.tehang.common.utility.event.mq.BroadcastingMqConsumer;
import com.tehang.common.utility.event.mq.ClusteringMqConsumer;
import com.tehang.common.utility.event.mq.MqConfig;
import com.tehang.common.utility.event.mq.MqProducer;
import com.tehang.common.utility.event.publish.EventPublisher;
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
  CommonRedisOperator.class
})
public @interface EnableDomainEvent {

}
