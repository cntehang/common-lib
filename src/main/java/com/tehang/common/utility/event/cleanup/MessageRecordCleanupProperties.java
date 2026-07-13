package com.tehang.common.utility.event.cleanup;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息记录清理配置.
 */
@Data
@ConfigurationProperties("tehang.message.cleanup")
public class MessageRecordCleanupProperties {

  /** 是否启用消息记录清理. */
  private boolean enabled;

  /** 数据保留月数. */
  private int retentionMonths = 3;

  /** 每批清理数量. */
  private int batchSize = 1000;

  /** 单次任务每类数据的最大清理批次数. */
  private int maxBatchesPerRun = 50;

  /** 清理任务cron表达式. */
  private String cron = "0 30 3 * * ?";

  /** 是否清理事件记录. */
  private boolean eventRecordEnabled = true;

  /** 是否清理消费记录. */
  private boolean consumeRecordEnabled;

  /** 是否清理命令记录及历史. */
  private boolean commandRecordEnabled;
}
