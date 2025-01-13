package com.tehang.common.utility.notification.wecom;

import org.springframework.web.client.RestTemplate;

/**
 * 发送企业微信消息使用的rest模板（不带LoadBalanced, 不通过consul, 而是直接访问)。
 */
public class WeComMessageSenderRestTemplate extends RestTemplate {

}
