package com.tehang.common.utility.event.subscriber;

/**
 * 使用数据库消费记录保证幂等的集群事件订阅者.
 */
public interface DatabaseIdempotentClusteringEventSubscriber extends ClusteringEventSubscriber {

}
