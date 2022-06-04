package com.tehang.common.utility.event.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 事务消息消息生产者.
 */
@Service
@Slf4j
public class TransactionMqProducer implements InitializingBean, DisposableBean {

  @Autowired
  private MqConfig mqConfig;

  @Autowired
  private LocalTransactionCheckerService localTransactionCheckerService;

  private TransactionProducer transactionProducer;

  /**
   * 事务第一次回查的时间，为相对时间。单位：秒
   */
  private static final int CHECK_IMMUNITY_TIME_IN_SECONDS = 1;

  private Properties getProducerProperties() {
    Properties properties = new Properties();

    properties.put(PropertyKeyConst.GROUP_ID, mqConfig.getGroupId());
    properties.put(PropertyKeyConst.AccessKey, mqConfig.getAccessKey());
    properties.put(PropertyKeyConst.SecretKey, mqConfig.getAccessSecretKey());
    // 推荐接入点设置方式
    properties.put(PropertyKeyConst.NAMESRV_ADDR, mqConfig.getNameServer());

    log.debug("TransactionProducer properties: {}", properties);
    return properties;
  }

  /**
   * 发送消息到队列.
   */
  public void sendToQueue(String tag, String key, String body, Long startDeliverTime) {
    String topic = mqConfig.getTopic();
    log.debug("Enter sendToQueue by transactionMqProducer, topic: {}, tag:{}, key:{}", topic, tag, key);

    SendResult result;
    try {
      Message msg = new Message(topic, tag, key, body.getBytes(StandardCharsets.UTF_8));
      // 设置事务第一次回查的时间，为相对时间。单位：秒
      // 第一次事务回查后如果消息没有提交或者回滚，则之后每隔1s左右会回查一次，共回查24小时。
      msg.putUserProperties(PropertyKeyConst.CheckImmunityTimeInSeconds, String.valueOf(CHECK_IMMUNITY_TIME_IN_SECONDS));

      if (startDeliverTime != null) {
        // 发送延时消息
        msg.setStartDeliverTime(startDeliverTime);
      }

      // 将本地事务状态设置为UnKnow, 依赖broker回查
      result = transactionProducer.send(msg, (msg1, arg) -> TransactionStatus.Unknow, null);
    }
    catch (Exception ex) {
      log.warn("sendToQueue by transactionMqProducer exception happen. ", ex);
      throw new MessageProducerException(ex.getMessage(), ex);
    }

    log.debug("Exit sendToQueue by transactionMqProducer, result:{}", result);
  }

  @Override
  public void afterPropertiesSet() {
    log.debug("TransactionMqProducer initializing...");
    transactionProducer = ONSFactory.createTransactionProducer(getProducerProperties(), localTransactionCheckerService);
    transactionProducer.start();
    log.debug("TransactionMqProducer initialized");
  }

  @Override
  public void destroy() {
    transactionProducer.shutdown();
    log.debug("TransactionMqProducer shutdown");
  }
}
