package com.tehang.common.utility;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 验证结果：包含验证是否成功标志，以及验证失败的errorMessage
 */
@Data
@NoArgsConstructor
public class ValidateResult implements Serializable {

  private static final long serialVersionUID = -8318026859383395475L;

  /**
   * 验证是否成功的标识
   */
  private boolean success;

  /**
   * 验证失败的消息提示
   */
  private String errorMessage;

  public static ValidateResult successOf() {
    ValidateResult result = new ValidateResult();
    result.success = true;
    result.errorMessage = StringUtils.EMPTY;
    return result;
  }

  public static ValidateResult errorOf(String errorMessage) {
    ValidateResult result = new ValidateResult();
    result.success = false;
    result.errorMessage = errorMessage;
    return result;
  }
}
