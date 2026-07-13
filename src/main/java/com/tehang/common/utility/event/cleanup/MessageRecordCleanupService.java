package com.tehang.common.utility.event.cleanup;

import com.tehang.common.utility.time.BjTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.function.IntSupplier;

/**
 * 消息记录清理服务.
 */
@Slf4j
public class MessageRecordCleanupService {

  private final MessageRecordCleanupProperties properties;

  private final MessageRecordCleanupJdbcRepository cleanupRepository;

  private final TransactionTemplate transactionTemplate;

  public MessageRecordCleanupService(MessageRecordCleanupProperties properties,
                                     MessageRecordCleanupJdbcRepository cleanupRepository,
                                     PlatformTransactionManager transactionManager) {
    this.properties = properties;
    this.cleanupRepository = cleanupRepository;
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  /** 执行消息记录清理. */
  public MessageRecordCleanupResult cleanup() {
    assertPropertiesValid();

    BjTime cutoffTime = BjTime.now().minusMonths(properties.getRetentionMonths());
    MessageRecordCleanupResult result = new MessageRecordCleanupResult();

    log.info("开始清理消息记录, cutoffTime: {}", cutoffTime);
    warnExpiredRecords(cutoffTime);
    cleanupCommandRecords(cutoffTime, result);
    cleanupConsumeRecords(cutoffTime, result);
    cleanupEventRecords(cutoffTime, result);
    return result;
  }

  private void cleanupCommandRecords(BjTime cutoffTime, MessageRecordCleanupResult result) {
    if (!properties.isCommandRecordEnabled()) {
      return;
    }

    BatchCleanupResult historyResult = cleanupInBatches(
      () -> cleanupRepository.deleteCommandHistoryBefore(cutoffTime, properties.getBatchSize()));
    result.addCommandHistoryCount(historyResult.recordCount);
    result.addCommandHistoryBatchCount(historyResult.batchCount);

    for (int batch = 0; batch < properties.getMaxBatchesPerRun(); batch++) {
      List<String> eventKeys = cleanupRepository.findExpiredCommandEventKeysBefore(cutoffTime, properties.getBatchSize());
      if (eventKeys.isEmpty()) {
        break;
      }

      CommandCleanupBatchResult batchResult = transactionTemplate.execute(status -> deleteCommandBatch(eventKeys));
      if (batchResult == null) {
        break;
      }

      result.addCommandHistoryCount(batchResult.commandHistoryCount);
      result.addCommandHistoryBatchCount(1);
      result.addCommandRecordCount(batchResult.commandRecordCount);
      result.addCommandRecordBatchCount(1);
      if (eventKeys.size() < properties.getBatchSize() || batchResult.commandRecordCount == 0) {
        break;
      }
    }
  }

  private CommandCleanupBatchResult deleteCommandBatch(List<String> eventKeys) {
    int historyCount = cleanupRepository.deleteCommandHistoryByEventKeys(eventKeys);
    int commandCount = cleanupRepository.deleteCommandRecordsByEventKeys(eventKeys);
    return new CommandCleanupBatchResult(commandCount, historyCount);
  }

  private void cleanupConsumeRecords(BjTime cutoffTime, MessageRecordCleanupResult result) {
    if (!properties.isConsumeRecordEnabled()) {
      return;
    }
    BatchCleanupResult cleanupResult = cleanupInBatches(
      () -> cleanupRepository.deleteConsumeRecordsBefore(cutoffTime, properties.getBatchSize()));
    result.addConsumeRecordCount(cleanupResult.recordCount);
    result.addConsumeRecordBatchCount(cleanupResult.batchCount);
  }

  private void cleanupEventRecords(BjTime cutoffTime, MessageRecordCleanupResult result) {
    if (!properties.isEventRecordEnabled()) {
      return;
    }
    BatchCleanupResult cleanupResult = cleanupInBatches(
      () -> cleanupRepository.deleteEventRecordsBefore(cutoffTime, properties.getBatchSize()));
    result.addEventRecordCount(cleanupResult.recordCount);
    result.addEventRecordBatchCount(cleanupResult.batchCount);
  }

  private BatchCleanupResult cleanupInBatches(IntSupplier deleteBatch) {
    int total = 0;
    int batchCount = 0;
    for (int batch = 0; batch < properties.getMaxBatchesPerRun(); batch++) {
      int count = deleteBatch.getAsInt();
      total += count;
      batchCount++;
      if (count < properties.getBatchSize()) {
        break;
      }
    }
    return new BatchCleanupResult(total, batchCount);
  }

  private void warnExpiredRecords(BjTime cutoffTime) {
    if (properties.isEventRecordEnabled()) {
      long waitSendCount = cleanupRepository.countExpiredWaitSendRecords(cutoffTime);
      if (waitSendCount > 0) {
        log.warn("存在超过保留期的待发送事件, count: {}, cutoffTime: {}", waitSendCount, cutoffTime);
      }
    }
    if (properties.isConsumeRecordEnabled()) {
      long processingCount = cleanupRepository.countExpiredProcessingRecords(cutoffTime);
      if (processingCount > 0) {
        log.warn("存在超过保留期的处理中消费记录, count: {}, cutoffTime: {}", processingCount, cutoffTime);
      }
    }
  }

  private void assertPropertiesValid() {
    if (properties.getRetentionMonths() <= 0) {
      throw new IllegalArgumentException("retentionMonths必须大于0");
    }
    if (properties.getBatchSize() <= 0) {
      throw new IllegalArgumentException("batchSize必须大于0");
    }
    if (properties.getMaxBatchesPerRun() <= 0) {
      throw new IllegalArgumentException("maxBatchesPerRun必须大于0");
    }
  }

  private static final class CommandCleanupBatchResult {

    private final int commandRecordCount;

    private final int commandHistoryCount;

    private CommandCleanupBatchResult(int commandRecordCount, int commandHistoryCount) {
      this.commandRecordCount = commandRecordCount;
      this.commandHistoryCount = commandHistoryCount;
    }
  }

  private static final class BatchCleanupResult {

    private final int recordCount;

    private final int batchCount;

    private BatchCleanupResult(int recordCount, int batchCount) {
      this.recordCount = recordCount;
      this.batchCount = batchCount;
    }
  }
}
