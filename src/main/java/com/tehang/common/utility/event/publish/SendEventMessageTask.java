package com.tehang.common.utility.event.publish;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 定时任务，发送事件消息到mq。
 */
@Slf4j
@Service
@AllArgsConstructor
public class SendEventMessageTask {

  /** 北京时区（上海）*/
  private static final String ZONE_SHANGHAI = "Asia/Shanghai";

  private final SendDomainEventRecordsToMqService sendDomainEventRecordsToMqService;

  /** 查找db中的待发送的领域事件记录，发送到mq。每2秒执行一次 */
  @Scheduled(cron = "0/2 * * * * ?", zone = ZONE_SHANGHAI)
  public void sendDomainEventRecordsToMq() {
    // 发送事件消息, 异步执行
    sendDomainEventRecordsToMqService.sendDomainEventRecords();

    log.debug("Exit sendDomainEventRecordsToMq");
  }
}
