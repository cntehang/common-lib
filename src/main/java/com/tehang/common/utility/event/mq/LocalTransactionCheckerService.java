package com.tehang.common.utility.event.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.tehang.common.utility.event.eventrecord.DomainEventRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class LocalTransactionCheckerService implements LocalTransactionChecker {

  private final DomainEventRecordRepository domainEventRecordRepository;

  /**
   * 回查本地事务，Broker回调Producer，将未结束的事务发给Producer，由Producer来再次决定事务是提交还是回滚
   *
   * @param msg 消息
   * @return {@link TransactionStatus} 事务状态, 包含提交事务、回滚事务、未知状态
   */
  @Override
  public TransactionStatus check(Message msg) {
    log.debug("Enter local transaction check, eventKey:{}", msg.getKey());

    var record = domainEventRecordRepository.findByEventKey(msg.getKey());
    if (record != null) {
      log.debug("checkLocalTransaction：CommitTransaction");
      return TransactionStatus.CommitTransaction;
    }

    log.debug("checkLocalTransaction：RollbackTransaction");
    return TransactionStatus.RollbackTransaction;
  }
}
