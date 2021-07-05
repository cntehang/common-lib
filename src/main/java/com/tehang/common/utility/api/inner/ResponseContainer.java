package com.tehang.common.utility.api.inner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tehang.common.utility.api.CommonCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Type;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * 通用的服务调用返回结果范型类，用于接收tmc调用其他服务或其他服务推送给tmc的数据.
 */
@Data
@JsonInclude(NON_NULL)
public class ResponseContainer<T> implements Serializable {

  private static final long serialVersionUID = -8318026859383395475L;

  private int code;

  private String message;

  private T data;

  /**
   * 构造方法一.
   */
  public ResponseContainer() {
    super();
  }

  /**
   * 构造方法二.
   */
  public ResponseContainer(int code, T data) {
    super();
    this.code = code;
    this.data = data;
  }

  /**
   * 构造方法三.
   */
  public ResponseContainer(int code, String message, T data) {
    super();
    this.code = code;
    this.message = message;
    this.data = data;
  }

  /**
   * 构造方法四.
   */
  public ResponseContainer(int code, String message) {
    super();
    this.code = code;
    this.message = message;
  }

  /**
   * 创建空的成功返回体对象.
   */
  public static ResponseContainer createSuccessEmptyResponse() {
    return new ResponseContainer(0, "FakeSuccess");
  }

  /**
   * 是否成功.
   */
  @JsonIgnore
  public boolean isSuccess() {
    return this.code == CommonCode.SUCCESS_CODE;
  }

  /**
   * 获取异常信息.
   *
   * @return 异常信息
   */
  @JsonIgnore
  public String getErrorMessage() {
    String errorMessage = StringUtils.EMPTY;

    if (!isSuccess()) {
      errorMessage = this.getMessage();
    }

    return errorMessage;
  }

  private Type type;

  private ResponseContainer(Type type) {
    this.type = type;
  }

  public static <T> ResponseContainer<T> forType(Type type) {
    return new ResponseContainer<T>(type) {
    };
  }
}
