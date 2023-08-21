package com.tehang.common.utility.event.wechatnotify;

import com.tehang.common.utility.baseclass.DtoBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发送企业微信通知的请求参数.
 */
@Getter
@Setter
public class WechatMessage extends DtoBase {

  @NotNull(message = "robot不能为空")
  @ApiModelProperty(value = "机器人枚举，通过此枚举可以获取机器人的webhook地址", required = true, example = "Ji_shu_bu_test")
  private WechatRobot robot;

  @NotBlank(message = "发送内容不能为空")
  @Length(max = 1000, message = "发送内容最长不超过1000个字符")
  @ApiModelProperty(value = "发送内容，微信的要求是最长不超过2048个字节，必须是utf8编码", required = true, example = "hello world!")
  private String message;

  // ----------- 方法 --------

  public static WechatMessage of(WechatRobot robot, String message) {
    var wechatMessage = new WechatMessage();
    wechatMessage.robot = robot;
    wechatMessage.message = message;
    return wechatMessage;
  }
}
