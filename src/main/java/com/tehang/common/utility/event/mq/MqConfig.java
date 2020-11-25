package com.tehang.common.utility.event.mq;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
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

  @Value("${aliyun.mq.main.groupId}")
  private String groupId;

  @Value("${aliyun.mq.main.eventTagPrefix}")
  private String eventTagPrefix;
}
