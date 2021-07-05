package com.tehang.common.utility.rest;

import com.google.common.collect.Lists;
import com.tehang.common.infrastructure.exceptions.ApplicationException;
import com.tehang.common.infrastructure.exceptions.BaseException;
import com.tehang.common.infrastructure.exceptions.ParameterException;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.api.CommonCode;
import com.tehang.common.utility.api.front.DataContainer;
import com.tehang.common.utility.rest.annotations.WrapException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller的通用异常处理.
 */
@Slf4j
@RestControllerAdvice(annotations = {WrapException.class})
public class RestControllerWrapExceptionAdvice {

  /**
   * 应用异常, code = ApplicationException.code.
   */
  @ExceptionHandler(ApplicationException.class)
  @ResponseStatus(HttpStatus.OK)
  public DataContainer handleApplicationException(ApplicationException ex) {
    var code = ex.getCode();
    var msg = ex.getMessage();
    log.debug("ApplicationException happen, code: {}, msg: {}", code, msg, ex);
    return createDataContainer(code, msg, ex);
  }

  /**
   * 数据访问异常, code = 97.
   */
  @ExceptionHandler({DataAccessException.class})
  @ResponseStatus(HttpStatus.OK)
  public DataContainer handleDataAccessException(DataAccessException ex) {
    var code = CommonCode.SQL_ERROR_CODE;
    var msg = CommonCode.SQL_ERROR_MESSAGE;
    // 数据持久层抛出的异常, 记录error级别日志
    log.error("DataAccessException happen, code: {}, msg: {}", code, msg, ex);
    return createDataContainer(code, msg, ex);
  }

  /**
   * 参数异常, code = 98.
   */
  @ExceptionHandler({ParameterException.class})
  @ResponseStatus(HttpStatus.OK)
  public DataContainer handleParameterException(ParameterException ex) {
    var code = CommonCode.PARAMETER_ERROR_CODE;
    var msg = ex.getMessage();
    log.debug("ParameterException happen, code: {}, msg: {}", code, msg, ex);
    return createDataContainer(code, msg, ex);
  }

  /**
   * 参数校验异常, code = 98.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.OK)
  public DataContainer handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    var fieldErrors = ex.getBindingResult().getFieldErrors();
    var error = fieldErrors.get(0);
    log.debug("请求参数验证失败，field: {}, value: {}, message: {}", error.getField(), error.getRejectedValue(), error.getDefaultMessage());

    // 参数校验出错码：98
    var code = CommonCode.PARAMETER_ERROR_CODE;
    var msg = error.getDefaultMessage();

    var container = new DataContainer(code, msg);
    container.setErrors(getErrors(fieldErrors));
    return container;
  }

  /**
   * 参数违反约束的异常, code = 98.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.OK)
  public DataContainer handleConstraintViolationException(ConstraintViolationException ex) {
    List<ConstraintViolation<?>> fieldErrors = Lists.newArrayList(ex.getConstraintViolations());
    var error = fieldErrors.get(0);
    log.debug("请求参数验证失败, message: {}, invalidValue: {}", error.getMessage(), error.getInvalidValue());

    // 参数校验出错码：98
    var code = CommonCode.PARAMETER_ERROR_CODE;
    var msg = error.getMessage();

    var container = new DataContainer(code, msg);
    container.setErrors(buildErrorMsg(fieldErrors));
    return container;
  }

  /**
   * 未知异常, code = 99.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.OK)
  public DataContainer handleCommonException(Exception ex) {
    var code = CommonCode.COMMON_ERROR_CODE;
    var msg = ex.getMessage();
    // 未知异常, 记录error级别日志
    log.error("Unknown Exception happen, code: {}, msg: {}", code, msg, ex);
    return createDataContainer(code, msg, ex);
  }

  // ------------ 一些辅助方法 ---------------

  private DataContainer createDataContainer(int code, String msg, Exception ex) {
    var dataContainer = new DataContainer(code, msg);
    dataContainer.setDebugMsg(getDebugMsg(ex));
    return dataContainer;
  }

  private String getDebugMsg(Exception ex) {
    var debugMsgMap = new HashMap<String, Object>();

    // 设置 debugMessage
    if (ex instanceof BaseException) {
      var baseException = (BaseException) ex;
      if (StringUtils.isNotBlank(baseException.getDebugMsg())) {
        debugMsgMap.put("message", baseException.getDebugMsg());
      }
    }

    // trace信息
    Map<String, String> traceMap = createTrace(ex);
    if (MapUtils.isNotEmpty(traceMap)) {
      debugMsgMap.put("trace", traceMap);
    }
    return JsonUtils.toJson(debugMsgMap);
  }

  private Map<String, String> createTrace(Exception ex) {
    Map<String, String> traceMap = new ConcurrentHashMap<>();

    var firstTrace = ex.getStackTrace()[0];
    String exTrace = Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).filter(msg -> msg.contains("com.tehang")).findFirst()
      .orElse(firstTrace.toString());

    if (StringUtils.isNotBlank(exTrace)) {
      traceMap.put("traceMessage", exTrace);
    }
    return traceMap;
  }

  private List<Object> getErrors(List<FieldError> errors) {
    List<Object> errMapList = new ArrayList<>();
    errors.forEach(fieldError -> {
      var value = new HashMap<String, String>();
      value.put("key", fieldError.getField());
      value.put("msg", fieldError.getDefaultMessage());
      errMapList.add(value);
    });
    return errMapList;
  }

  private List<Object> buildErrorMsg(List<ConstraintViolation<?>> errors) {
    List<Object> errMapList = new ArrayList<>();
    errors.forEach(fieldError -> {
      var value = new HashMap<String, String>();
      value.put("key", getFieldName(fieldError));
      value.put("msg", fieldError.getMessage());
      errMapList.add(value);
    });
    return errMapList;
  }

  private String getFieldName(ConstraintViolation<?> violation) {
    PathImpl propertyPath = (PathImpl) violation.getPropertyPath();
    return propertyPath.getLeafNode().getName();
  }
}