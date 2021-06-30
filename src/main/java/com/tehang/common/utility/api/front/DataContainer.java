package com.tehang.common.utility.api.front;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tehang.common.utility.api.CommonCode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * 通用型数据返回，只用于封装数据返回给前端.
 */
@Data
@JsonInclude(NON_NULL)
@SuppressWarnings({"PMD.BeanMembersShouldSerialize", "PMD.SingularField"})
public class DataContainer implements Serializable {

  private static final long serialVersionUID = -8318026859383395475L;

  private int code;

  private String message;

  private String debugMsg;

  private List<Object> errors;

  private Object data;

  /**
   * 默认构造函数. 默认成功.
   */
  public DataContainer() {
    this.code = CommonCode.SUCCESS_CODE;
    this.message = CommonCode.SUCCESS_MESSAGE;
  }

  /**
   * 只有code的构造函数.
   */
  public DataContainer(int code) {
    this.code = code;
    this.message = "";
  }

  /**
   * 拥有code、message 的构造函数.
   *
   * @param code    int code
   * @param message string message
   */
  public DataContainer(int code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * DataContainer.
   */
  public DataContainer(int code, String message, Object data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  /**
   * 构造成功的结果.
   */
  public DataContainer(Object data) {
    this();
    this.data = data;
  }
}
