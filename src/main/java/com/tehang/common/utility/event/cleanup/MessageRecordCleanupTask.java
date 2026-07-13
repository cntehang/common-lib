package com.tehang.common.utility.event.cleanup;

import com.tehang.common.utility.lock.DistributedLockHelper;
import com.tehang.common.utility.lock.LockNotAcquiredException;
import com.tehang.common.utility.time.BjTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 消息记录定时清理任务.
 */
@Slf4j
@AllArgsConstructor
public class MessageRecordCleanupTask {

  private static final String ZONE_SHANGHAI = "Asia/Shanghai";

  private static final String LOCK_ID = "MessageRecordCleanupTask";

  private static final long LOCK_EXPIRED_SECONDS = 30 * 60;

  private final MessageRecordCleanupService cleanupService;

  private final DistributedLockHelper lockHelper;

  /** 每日分批清理超过保留期的消息记录. */
  @Scheduled(cron = "${tehang.message.cleanup.cron:0 30 3 * * ?}", zone = ZONE_SHANGHAI)
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void cleanupMessageRecords() {
    BjTime startTime = BjTime.now();
    try {
      MessageRecordCleanupResult result = lockHelper.withLock(
          LOCK_ID, false, LOCK_EXPIRED_SECONDS, cleanupService::cleanup);
      if (log.isInfoEnabled()) {
        log.info("消息记录清理完成, eventRecordCount: {}, eventRecordBatchCount: {}, consumeRecordCount: {}, "
                + "consumeRecordBatchCount: {}, commandRecordCount: {}, commandRecordBatchCount: {}, "
                + "commandHistoryCount: {}, commandHistoryBatchCount: {}, elapsed: {}s",
            result.getEventRecordCount(), result.getEventRecordBatchCount(), result.getConsumeRecordCount(),
            result.getConsumeRecordBatchCount(), result.getCommandRecordCount(), result.getCommandRecordBatchCount(),
            result.getCommandHistoryCount(), result.getCommandHistoryBatchCount(), BjTime.elapsedSeconds(startTime));
      }
    }
    catch (LockNotAcquiredException ex) {
      log.debug("消息记录清理任务未获取到分布式锁, 本次跳过");
    }
    catch (Exception ex) {
      log.error("消息记录清理失败, message: {}", ex.getMessage(), ex);
    }
  }
}
