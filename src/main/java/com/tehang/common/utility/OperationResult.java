package com.tehang.common.utility;

import com.tehang.common.utility.baseclass.DtoBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 表示一个通用的操作结果，包含是否成功的标记，以及提示消息。
 */
@Getter
@Setter
public class OperationResult extends DtoBase {

  @ApiModelProperty(value = "操作是否成功", required = true, example = "true")
  private boolean success;

  @ApiModelProperty(value = "操作失败的描述消息", example = "操作失败")
  private String errorMessage;

  // ----------- 方法 ----------

  public static OperationResult of(boolean success, String errorMessage) {
    var result = new OperationResult();
    result.success = success;
    result.errorMessage = errorMessage;
    return result;
  }

  public static OperationResult successOf() {
    return of(true, StringUtils.EMPTY);
  }

  public static OperationResult errorOf(String errorMessage) {
    return of(false, errorMessage);
  }
}
