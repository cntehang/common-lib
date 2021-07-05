package com.tehang.common.utility.event.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 消息生产者.
 */
@Service
@Slf4j
public class MqProducer implements InitializingBean, DisposableBean {

  @Autowired
  private MqConfig mqConfig;

  private Producer producer;

  @Override
  public void afterPropertiesSet() {
    log.debug("MqProducer initializing...");
    producer = ONSFactory.createProducer(getProducerProperties());
    producer.start();
    log.debug("MqProducer initialized");
  }

  private Properties getProducerProperties() {
    Properties properties = new Properties();
    properties.put(PropertyKeyConst.GROUP_ID, mqConfig.getGroupId());
    properties.put(PropertyKeyConst.AccessKey, mqConfig.getAccessKey());
    properties.put(PropertyKeyConst.SecretKey, mqConfig.getAccessSecretKey());
    // 推荐接入点设置方式
    properties.put(PropertyKeyConst.NAMESRV_ADDR, mqConfig.getNameServer());

    log.debug("MqProducer properties: {}", properties);
    return properties;
  }

  /**
   * 发送消息到队列.
   */
  @SuppressWarnings("all")
  public SendResult sendToQueue(String tag, String key, String body) {
    String topic = mqConfig.getTopic();
    log.debug("Enter sendToQueue, topic: {}, tag:{}, key:{}", topic, tag, key);

    SendResult result;
    try {
      Message msg = new Message(topic, tag, key, body.getBytes(Charset.forName("UTF-8")));
      result = producer.send(msg);
    }
    catch (Exception ex) {
      log.warn("sendToQueue exception heppen. ", ex);
      throw new MessageProducerException(ex.getMessage(), ex);
    }

    log.debug("Exit sendToQueue, result:{}", result);
    return result;
  }

  @Override
  public void destroy() {
    producer.shutdown();
    log.debug("MqProducer shutdown");
  }
}
