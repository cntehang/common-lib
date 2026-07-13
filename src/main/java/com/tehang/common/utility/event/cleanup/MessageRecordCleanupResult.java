package com.tehang.common.utility.event.cleanup;

import lombok.Getter;

/**
 * 消息记录清理结果.
 */
@Getter
public class MessageRecordCleanupResult {

  private int eventRecordCount;

  private int eventRecordBatchCount;

  private int consumeRecordCount;

  private int consumeRecordBatchCount;

  private int commandRecordCount;

  private int commandRecordBatchCount;

  private int commandHistoryCount;

  private int commandHistoryBatchCount;

  void addEventRecordCount(int count) {
    eventRecordCount += count;
  }

  void addEventRecordBatchCount(int count) {
    eventRecordBatchCount += count;
  }

  void addConsumeRecordCount(int count) {
    consumeRecordCount += count;
  }

  void addConsumeRecordBatchCount(int count) {
    consumeRecordBatchCount += count;
  }

  void addCommandRecordCount(int count) {
    commandRecordCount += count;
  }

  void addCommandRecordBatchCount(int count) {
    commandRecordBatchCount += count;
  }

  void addCommandHistoryCount(int count) {
    commandHistoryCount += count;
  }

  void addCommandHistoryBatchCount(int count) {
    commandHistoryBatchCount += count;
  }
}
