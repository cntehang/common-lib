package com.tehang.common.utility.notification.wecom;

import com.tehang.common.utility.baseclass.DtoBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * 发送企业微信通知的请求参数
 */
@Getter
@Setter
public class WeComMessage extends DtoBase {

  @ApiModelProperty(value = "企业微信群机器人的webhook，如果为空，将忽略发送请求。")
  private String webhook;

  @Length(max = 1000, message = "发送内容最长不超过1000个字符")
  @ApiModelProperty(value = "发送内容，微信的要求是最长不超过2048个字节，必须是utf8编码。如果为空，将忽略发送请求。")
  private String message;

  // ----------- 方法 --------

  public static WeComMessage of(String webhook, String message) {
    var weComMessage = new WeComMessage();
    weComMessage.webhook = webhook;
    weComMessage.message = message;
    return weComMessage;
  }
}
