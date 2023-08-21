package com.tehang.common.utility.event.wechatnotify;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 企业微信机器人枚举.
 */
@Getter
@AllArgsConstructor
public enum WechatRobot {

  /**
   * 技术部测试.
   */
  Ji_shu_bu_test("技术部-机器人-test", "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=b27e3332-1904-4f4f-a95e-def3487ba1a7"),

  /**
   * 国内机票抢票通知.
   */
  Flight_Grabbing_Tickets_Notify("国内机票抢票通知", "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=f65f49ba-277d-4796-8813-ec290f7dcbd9"),

  /**
   * 业务告警通知.
   */
  Biz_Warning_Notify("业务告警通知", "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=ab2f3fbf-371a-4615-8550-1c81fd4bccb6"),

  /**
   * 生产环境告警通知.
   */
  Prd_Env_Error_Message_Notify("生产环境告警通知", "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=313ddc48-f664-4f60-849e-5aaab0edf8e0");

  private final String description;
  private final String webhook;
}
