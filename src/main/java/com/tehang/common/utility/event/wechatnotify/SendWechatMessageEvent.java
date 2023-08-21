package com.tehang.common.utility.event.wechatnotify;

import com.tehang.common.utility.event.DomainEvent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 发送企业微信消息的事件参数类.
 */
@Getter
@Setter
@NoArgsConstructor
public class SendWechatMessageEvent extends DomainEvent {

  /**
   * 【发送企业微信消息】事件类型名
   */
  public static final String EVENT_TYPE = "BRS-SendWechatMessage";

  @NotNull(message = "发送内容不能为空")
  @ApiModelProperty(value = "发送内容", required = true)
  private WechatMessage wechatMessage;

  public SendWechatMessageEvent(WechatMessage wechatMessage) {
    super(EVENT_TYPE);
    this.wechatMessage = wechatMessage;
  }
}
