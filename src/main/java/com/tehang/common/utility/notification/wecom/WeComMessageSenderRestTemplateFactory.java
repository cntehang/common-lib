package com.tehang.common.utility.notification.wecom;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 发送企业微信消息的rest模板的工厂类（不带LoadBalanced, 不通过consul, 而是直接访问)。
 */
@Configuration
@AllArgsConstructor
public class WeComMessageSenderRestTemplateFactory {

  private RestTemplateBuilder restTemplateBuilder;

  /** 构造发送企业微信消息使用的RestTemplate。*/
  @Bean
  public WeComMessageSenderRestTemplate weComMessageSenderRestTemplate() {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(10))// 连接超时设为10s
        .setReadTimeout(Duration.ofSeconds(60))// 请求超时设为60s
        .build(WeComMessageSenderRestTemplate.class);
  }
}
