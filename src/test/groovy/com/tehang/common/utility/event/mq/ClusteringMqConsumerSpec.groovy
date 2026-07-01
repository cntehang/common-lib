package com.tehang.common.utility.event.mq

import com.tehang.common.TestSpecification
import com.tehang.common.infrastructure.exceptions.SystemErrorException
import com.tehang.common.utility.event.DefaultEvent
import com.tehang.common.utility.event.consume.DomainEventConsumeService
import com.tehang.common.utility.event.subscriber.ClusteringEventSubscriber
import com.tehang.common.utility.event.subscriber.DatabaseIdempotentClusteringEventSubscriber
import com.tehang.common.utility.lock.DistributedLock
import com.tehang.common.utility.lock.DistributedLockFactory
import org.springframework.data.redis.core.BoundValueOperations
import org.springframework.data.redis.core.StringRedisTemplate

import java.time.Duration
import java.util.concurrent.TimeUnit

class ClusteringMqConsumerSpec extends TestSpecification {

  StringRedisTemplate redisTemplate = Mock(StringRedisTemplate)
  DistributedLockFactory lockFactory = Mock(DistributedLockFactory)
  DomainEventConsumeService domainEventConsumeService = Mock(DomainEventConsumeService)
  ClusteringMqConsumer consumer = new ClusteringMqConsumer()

  def setup() {
    setField(consumer, 'redisTemplate', redisTemplate)
    setField(consumer, 'lockFactory', lockFactory)
    setField(consumer, 'domainEventConsumeService', domainEventConsumeService)
  }

  def "普通订阅者使用eventType和eventKey粒度的分布式锁与Redis防重"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'
    ClusteringEventSubscriber subscriber = Mock(ClusteringEventSubscriber)
    DistributedLock lock = Mock(DistributedLock)
    BoundValueOperations<String, String> redisOps = Mock(BoundValueOperations)

    when:
    consumer.consumeEvent(subscriber, event, 'FlightBookingCompleted')

    then:
    1 * lockFactory.acquireLockUnBlocked('MQ_Consumer_Lock_FlightBookingCompleted_order-1',
        Duration.ofMinutes(15)) >> lock
    2 * subscriber.getInstanceId() >> 'test-subscriber'
    1 * redisTemplate.boundValueOps('MQ_Consumer_Idempotent_FlightBookingCompleted_order-1') >> redisOps
    1 * redisOps.get() >> null
    1 * subscriber.handleEvent(event)
    1 * redisOps.set('OK', 24, TimeUnit.HOURS)
    1 * lock.close()
    0 * domainEventConsumeService._
  }

  def "普通订阅者Redis已消费时跳过业务处理"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'
    ClusteringEventSubscriber subscriber = Mock(ClusteringEventSubscriber)
    DistributedLock lock = Mock(DistributedLock)
    BoundValueOperations<String, String> redisOps = Mock(BoundValueOperations)

    when:
    consumer.consumeEvent(subscriber, event, 'FlightBookingCompleted')

    then:
    1 * lockFactory.acquireLockUnBlocked('MQ_Consumer_Lock_FlightBookingCompleted_order-1',
        Duration.ofMinutes(15)) >> lock
    2 * subscriber.getInstanceId() >> 'test-subscriber'
    1 * redisTemplate.boundValueOps('MQ_Consumer_Idempotent_FlightBookingCompleted_order-1') >> redisOps
    1 * redisOps.get() >> 'OK'
    0 * subscriber.handleEvent(_)
    0 * redisOps.set(_, _, _)
    1 * lock.close()
    0 * domainEventConsumeService._
  }

  def "数据库幂等订阅者进入DomainEventConsumeService且不使用Redis和分布式锁"() {
    given:
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'
    DatabaseIdempotentClusteringEventSubscriber subscriber = Mock(DatabaseIdempotentClusteringEventSubscriber)

    when:
    consumer.consumeEvent(subscriber, event, 'FlightBookingCompleted')

    then:
    2 * subscriber.subscriberId() >> 'tmc-services.flight-booking-completed'
    1 * domainEventConsumeService.consume(subscriber, event) >> true
    0 * lockFactory._
    0 * redisTemplate._
    0 * subscriber.handleEvent(_)
  }

  def "数据库幂等订阅者缺少消费服务时抛出明确异常"() {
    given:
    setField(consumer, 'domainEventConsumeService', null)
    def event = new DefaultEvent('FlightBookingCompleted')
    event.key = 'order-1'
    DatabaseIdempotentClusteringEventSubscriber subscriber = Mock(DatabaseIdempotentClusteringEventSubscriber)

    when:
    consumer.consumeEvent(subscriber, event, 'FlightBookingCompleted')

    then:
    thrown(SystemErrorException)
    0 * lockFactory._
    0 * redisTemplate._
    0 * subscriber.handleEvent(_)
  }

  private static void setField(Object target, String name, Object value) {
    def field = target.class.getDeclaredField(name)
    field.accessible = true
    field.set(target, value)
  }
}
