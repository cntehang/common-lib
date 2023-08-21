package com.tehang.common.utility.erroralarm;

import com.tehang.common.utility.event.publish.TransactionalEventPublisher;
import com.tehang.common.utility.event.wechatnotify.SendWechatMessageEvent;
import com.tehang.common.utility.event.wechatnotify.WechatMessage;
import com.tehang.common.utility.event.wechatnotify.WechatRobot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.left;

/**
 * 企业微信线上告警发送实现类.
 */
@Service
@AllArgsConstructor
@Slf4j
public class WeChatErrorAlarmSendAppender extends AbstractErrorAlarmSendAppender {

  private static final int MAX_ERROR_MESSAGE_LENGTH = 200;
  private static final int MAX_STACK_TEXT_LENGTH = 600;

  private TransactionalEventPublisher eventPublisher;

  /**
   * 发送告警.
   */
  @Override
  protected void sendAlarmMessage(AlarmMessage alarmMessage) {
    log.debug("Enter sendAlarmMessage, alarmMessage:{}.", alarmMessage);

    var wechatMessage = createWechatMessage(alarmMessage);
    publishSendWechatMessageEvent(wechatMessage);

    log.debug("Exit sendAlarmMessage.");
  }

  private static WechatMessage createWechatMessage(AlarmMessage alarmMessage) {
    var result = new WechatMessage();

    result.setRobot(WechatRobot.Prd_Env_Error_Message_Notify);

    String sb = "服务名称: " + defaultIfBlank(alarmMessage.getServiceName(), StringUtils.EMPTY) + StringUtils.LF
        + "调用方法: " + defaultIfBlank(alarmMessage.getMethodName(), StringUtils.EMPTY) + StringUtils.LF
        + "时间: " + alarmMessage.getCreateTime() + StringUtils.LF
        + "TraceId: " + defaultIfBlank(alarmMessage.getTraceId(), StringUtils.EMPTY) + StringUtils.LF
        + "错误消息: " + defaultIfBlank(left(alarmMessage.getErrorMessage(), MAX_ERROR_MESSAGE_LENGTH), StringUtils.EMPTY) + StringUtils.LF
        + "异常堆栈: " + left(alarmMessage.getStackText(), MAX_STACK_TEXT_LENGTH);
    result.setMessage(sb);

    return result;
  }

  /**
   * 发布发送企业微信消息事件.
   */
  private void publishSendWechatMessageEvent(WechatMessage wechatMessage) {
    log.debug("Enter publishSendWechatMessageEvent, wechatMessage:{}.", wechatMessage);

    eventPublisher.publish(new SendWechatMessageEvent(wechatMessage));

    log.debug("Exit publishSendWechatMessageEvent.");
  }
}

