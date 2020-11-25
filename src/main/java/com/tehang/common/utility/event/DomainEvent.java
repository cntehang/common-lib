package com.tehang.common.utility.event;

import com.tehang.common.utility.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 事件参数的抽象基类, 包含事件相关的数据
 */
@Getter
@Setter
public abstract class DomainEvent {

  /**
   * 事件的唯一标识，业务相关的标识，并非阿里云mq的message_id
   */
  private String key;

  /**
   * 事件类型，和mq中的tag保持一致(tag可能包含前缀，但eventType字段并不包含前缀)
   */
  private String eventType;

  /**
   * 事件发布时间(北京时间，格式为yyyy-MM-dd HH:mm:ss.SSS)
   */
  private String publishTime;

  /**
   * 事件发布者(对应于mq中的groupId)
   */
  private String publisher;

  //-------------- 构造函数 --------------
  /**
   * 默认构造函数
   */
  public DomainEvent() {
    // do nothing
  }

  /**
   * 通过EventType构造实例，key取uuid
   */
  public DomainEvent(String eventType) {
    this.key = UUID.randomUUID().toString();
    this.eventType = eventType;
  }

  //-------------- 其他 --------------
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
