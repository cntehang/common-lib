package com.tehang.common.utility.event.command;

import lombok.Getter;
import lombok.Setter;

/**
 * 携带orderId的命令参数。
 */
@Getter
@Setter
public class OrderIdCommandArgs extends CommandArgs {

  /** 订单id */
  private long orderId;

  // ----------- 方法 ----------

  public static OrderIdCommandArgs of(long orderId) {
    var result = new OrderIdCommandArgs();
    result.orderId = orderId;
    return result;
  }
}
