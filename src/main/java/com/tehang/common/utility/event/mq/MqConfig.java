package com.tehang.common.utility.event.mq;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * MQ CONFIG.
 * @ Date       ：Created in 16:44 2018/10/8
 * @ Description：消息队列配置
 */
@Data
@Configuration
public class MqConfig {

  @Value("${aliyun.mq.accessKey}")
  private String accessKey;

  @Value("${aliyun.mq.accessSecretKey}")
  private String accessSecretKey;

  @Value("${aliyun.mq.nameServer}")
  private String nameServer;

  @Value("${aliyun.mq.main.topic}")
  private String topic;

  /**
   * groupId有两个作用： 1. 作为消息发布者 2. 作为集群消息订阅者id(集群订阅模式): 每个服务都应定义不同的groupId
   */
  @Value("${aliyun.mq.main.groupId}")
  private String groupId;

  /**
   * 广播消息订阅者id: 每个服务都应定义不同的broadcastingGroupId, 必须与集群模式的订阅者id不同.
   */
  @Value("${aliyun.mq.main.broadcastingGroupId:#{null}}")
  private String broadcastingGroupId;

  /**
   * domainEvent对应的tag前缀: 用来在不同环境共用topic, 每套环境中的各个服务需要定义相同的eventTagPrefix.
   */
  @Value("${aliyun.mq.main.eventTagPrefix}")
  private String eventTagPrefix;
}
