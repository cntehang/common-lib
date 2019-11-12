package com.tehang.common.utility.http;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * rest模板
 */
@Profile("!integration_test")
@Configuration
public class RestTemplateFactory {

  /**
   * rest模板构建器
   */
  private RestTemplateBuilder restTemplateBuilder;

  /**
   * 不带lb的client
   */
  private RestTemplate restTemplate;

  /**
   * constructor DI
   *
   * @param restTemplateBuilder
   */
  RestTemplateFactory(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplateBuilder = restTemplateBuilder;
  }

  /**
   * 此适用于内部访问请求.
   * 设置超时内部超时时间
   *
   * @return restTemplate
   */
  @Bean
  @LoadBalanced
  public RestTemplate getRestTemplate() {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(10))// 连接超时设为10s
        .setReadTimeout(Duration.ofSeconds(60))// 请求超时设为60s
        .build();
  }

  /**
   * 这里适用于外部反问请求。
   *
   * @return
   */
  public RestTemplate getClientWithoutRibbon() {
    if (restTemplate == null) {
      restTemplate = restTemplateBuilder
          .setConnectTimeout(Duration.ofSeconds(10))// 连接超时设为10s
          .setReadTimeout(Duration.ofSeconds(60))// 请求超时设为60s
          .build();
    }
    return restTemplate;
  }

  /**
   * 获取一个新的client
   *
   * @return
   */
  public RestTemplate getNewClient() {
    return restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(30))// 连接超时设为10s
        .setReadTimeout(Duration.ofSeconds(60))// 请求超时设为60s
        .build();
  }
}
