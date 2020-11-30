/*
 * Copyright (c) 2020 MAMCHARGE 深圳智链物联科技有限公司. <http://www.mamcharge.com>
 *
 */

package com.tehang.common.utility.api.inner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tehang.common.utility.api.CommonCode;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * 空的返回结果类：不包含data字段
 */
@Data
@JsonInclude(NON_NULL)
public class EmptyResponseContainer {

  /**
   * 返回结果代码
   */
  private int code;

  /**
   * 返回结果消息
   */
  private String message;

  /**
   * 返回结果消息
   */
  private String debugMsg;

  /**
   * 是否成功
   */
  @JsonIgnore
  public boolean isSuccess() {
    return this.code == CommonCode.SUCCESS_CODE;
  }

}
