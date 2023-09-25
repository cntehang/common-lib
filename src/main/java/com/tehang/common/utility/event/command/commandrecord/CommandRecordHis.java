package com.tehang.common.utility.event.command.commandrecord;

import com.tehang.common.utility.baseclass.EntityObject;
import com.tehang.common.utility.time.BjTime;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * 命令的执行历史记录。
 */
@Slf4j
@Getter
@Setter
public class CommandRecordHis extends EntityObject {

  /** PK, uuid */
  @Id
  @Column(nullable = false, length = 50)
  private String id;

  /** 命令id, FK, 引用Command表 */
  @Column(nullable = false, length = 50)
  private String commandId;

  /** 执行是否成功 */
  private boolean success;

  /** 执行失败时的错误消息 */
  @Column(length = 300)
  private String errorMessage;

  /** 创建时间 */
  @Column(nullable = false, length = 23)
  private BjTime createTime;

  // ------------- 方法 ------------

}
