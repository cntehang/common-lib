package com.tehang.common.utility.erroralarm;

import com.tehang.common.utility.baseclass.DtoBase;
import com.tehang.common.utility.external.ExternalServiceProxy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 企业微信线上告警发送实现类.
 */
@Service
@AllArgsConstructor
@Slf4j
public class WeChatErrorAlarmSendAppender extends AbstractErrorAlarmSendAppender {

  private ExternalServiceProxy externalServiceProxy;

  /**
   * 发送告警.
   */
  @Override
  protected void sendAlarmMessage(String alarmMessage, String notifyUrl) {
    log.debug("Enter sendAlarmMessage, alarmMessage:{}，notifyUrl：{}。", alarmMessage, notifyUrl);

    if (StringUtils.isNotBlank(notifyUrl)) {
      var wechatMessageBody = createWechatMessageBody(alarmMessage);
      externalServiceProxy.post(notifyUrl, new HttpEntity<>(wechatMessageBody), new ParameterizedTypeReference<WechatResult>() {});
    }

    log.debug("Exit sendAlarmMessage.");
  }

  private WechatMessageBody createWechatMessageBody(String alarmMessage) {
    WechatMessageBody body = new WechatMessageBody();

    body.msgtype = "text";
    body.text = WechatText.of(alarmMessage, List.of("@all"));

    return body;
  }

  @Getter
  @Setter
  public static class WechatMessageBody extends DtoBase {
    /** 消息类型 */
    private String msgtype;

    /** 消息文本 */
    private WechatText text;
  }

  @Getter
  @Setter
  public static class WechatText extends DtoBase {
    /** 消息内容 */
    private String content;

    /** userid的列表，提醒群中的指定成员(@某个成员)，@all表示提醒所有人，如果开发者获取不到userid，可以使用mentioned_mobile_list */
    private List<String> mentioned_list;

    public static WechatText of(String content, List<String> mentionedList) {
      var wechatText = new WechatText();
      wechatText.content = content;
      wechatText.mentioned_list = mentionedList;
      return wechatText;
    }
  }

  @Getter
  @Setter
  public static class WechatResult extends DtoBase {
    /** 错误代码 */
    private Integer errcode;

    /** 错误消息 */
    private String errmsg;
  }
}

