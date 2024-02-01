package com.tehang.common.utility.erroralarm;

import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.StringUtils;
import com.tehang.common.utility.baseclass.DtoBase;
import com.tehang.common.utility.http.HttpClientUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 企业微信线上告警发送实现类.
 */
@Slf4j
public class WeChatErrorAlarmSendAppender extends AbstractErrorAlarmSendAppender {

  /**
   * 发送告警.
   */
  @Override
  protected void sendAlarmMessage(String alarmMessage, String notifyUrl) {
    log.debug("Enter sendAlarmMessage, alarmMessage:{}，notifyUrl：{}。", alarmMessage, notifyUrl);

    if (isNotBlank(notifyUrl)) {
      try {
        var wechatMessageBody = createWechatMessageBody(formatAlarmMessage(alarmMessage));
        var result = HttpClientUtils.getInstance().httpPost(notifyUrl, JsonUtils.toJson(wechatMessageBody));

        WechatResult resp = JsonUtils.toClass(result, WechatResult.class);
        if (resp == null || resp.getErrcode() != 0) {
          super.addError(String.format("发送企业微信线上告警失败, 原因:%s", resp));
        }
      }
      catch (Exception ex) {
        super.addError(String.format("发送企业微信线上告警失败, 原因:%s", ex.getMessage()), ex);
      }
    }
    log.debug("Exit sendAlarmMessage.");
  }

  private String formatAlarmMessage(String alarmMessage) {
    if (isNotBlank(alarmMessage) && alarmMessage.length() > 1000) {
      return String.format("%s...", StringUtils.left(alarmMessage, 1000));
    }
    return alarmMessage;
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

