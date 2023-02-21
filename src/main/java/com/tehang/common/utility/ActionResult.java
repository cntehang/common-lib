package com.tehang.common.utility;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 表示一个动作的执行结果，包含是否执行成功的标识，以及错误消息。
 */
@Getter
@Setter
public class ActionResult implements Serializable {

  private static final long serialVersionUID = -5554308939380869754L;

  @ApiModelProperty(value = "操作是否成功", required = true, example = "true")
  private boolean success;

  @ApiModelProperty(value = "操作失败的描述消息", example = "操作失败")
  private String errorMessage;

  // ----------- 方法 ----------

  public static ActionResult of(boolean success, String errorMessage) {
    var result = new ActionResult();
    result.success = success;
    result.errorMessage = errorMessage;
    return result;
  }

  public static ActionResult successOf() {
    return of(true, StringUtils.EMPTY);
  }

  public static ActionResult errorOf(String errorMessage) {
    return of(false, errorMessage);
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
