package com.tehang.common.utility.event.command.commandrecord;

import com.tehang.common.utility.baseclass.AggregateRoot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * 命令记录。
 */
@Slf4j
@Getter
@Setter
public class CommandRecord extends AggregateRoot<String> {

  /** PK, uuid */
  @Id
  @Column(nullable = false, length = 50)
  private String id;

  /** 事件key, 此命令对应的事件key，同一个事件key下可以有多个命令，这些命令将按顺序执行。*/
  @Column(nullable = false, length = 100)
  private String eventKey;

  /** 命令类型, 执行命令时，根据该类型查找指定的命令bean。*/
  @Column(nullable = false, length = 100)
  private String commandType;

  /** 命令参数对象的json表示 */
  @Column(columnDefinition = "TEXT")
  private String commandArgs;

  /** 命令返回对象的json表示 */
  @Column(columnDefinition = "TEXT")
  private String commandReturnValue;

  /** 执行是否成功 */
  private boolean success;

  /** 命令的顺序号，多个命令按顺序执行, 顺序号从1开始 */
  private int seqNo;

  // ------------- 方法 ------------

}
