package com.tehang.common.utility.notification.wecom;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import com.tehang.common.utility.baseclass.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tehang.common.utility.StringUtils.isBlank;

/**
 * 企业微信群消息发送的辅助类。
 **/
@Service
@AllArgsConstructor
@Slf4j
public class WeComMessageSender {

  private WeComMessageSenderRestTemplate restTemplate;

  /** 发送企业微信消息 */
  public void sendWeComMessage(WeComMessage weComMessage) {
    log.debug("Enter sendWeComMessage, weComMessage: {}", weComMessage);

    if (weComMessage == null || isBlank(weComMessage.getWebhook()) || isBlank(weComMessage.getMessage())) {
      log.warn("webHook或message参数为空，企业微信消息被忽略。weComMessage: {}", weComMessage);
      return;
    }

    sendMessage(weComMessage);
    log.debug("Exit sendWechatMessage for success, weComMessage: {}", weComMessage);
  }

  private void sendMessage(WeComMessage weComMessage) {
    // 构造消息发送内容
    var wechatMessageBody = createWechatMessage(weComMessage);

    // 发送消息
    ResponseEntity<WechatResult> exchange = restTemplate.exchange(
        weComMessage.getWebhook(), HttpMethod.POST, new HttpEntity<>(wechatMessageBody),
        new ParameterizedTypeReference<WechatResult>() {}
    );
    WechatResult resp = exchange.getBody();
    log.debug("sendWeComMessage response: {}", resp);

    if (resp == null || resp.getErrcode() != 0) {
      var msg = String.format("sendWeComMessage failed, response: %s", resp);
      log.warn(msg);
      throw new SystemErrorException(msg);
    }
  }

  private WechatMessageBody createWechatMessage(WeComMessage request) {
    WechatMessageBody body = new WechatMessageBody();
    body.msgtype = "text";
    body.text = WechatText.of(request.getMessage(), List.of("@all"));
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
