package com.tehang.common.infrastructure.exceptions.handler;

import com.google.common.collect.Lists;
import com.tehang.common.utility.api.CommonCode;
import com.tehang.common.utility.api.front.DataContainer;
import com.tehang.common.utility.consts.ExceptionHandlerConstants;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller层的异常处理，主要用于处理参数验证方面的错误.
 */
@RestControllerAdvice
@Component
public class ControllerExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  /**
   * 转换单一属性Request的校验失败结果，如string，int等.
   */
  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public DataContainer handle(HttpServletRequest request, HttpServletResponse response, ConstraintViolationException exception) {
    List<ConstraintViolation> fieldErrors = Lists.newArrayList(exception.getConstraintViolations());
    ConstraintViolation error = fieldErrors.get(0);
    DataContainer container = new DataContainer(CommonCode.PARAMETER_ERROR_CODE, error.getMessage());

    // error message header 用于在filter中判断请求响应是否出现异常
    attachErrorHeader(response);
    container.setErrors(buildErrorMsg(fieldErrors));

    LOG.info("请求参数验证失败, message: {}", error.getMessage());
    return container;
  }

  /**
   * 转换对象类Request的校验失败结果.
   */
  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public DataContainer handle(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException exception) {
    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
    FieldError error = fieldErrors.get(0);
    DataContainer container = new DataContainer(CommonCode.PARAMETER_ERROR_CODE, error.getDefaultMessage());

    // error message header 用于在filter中判断请求响应是否出现异常
    attachErrorHeader(response);
    container.setErrors(error(fieldErrors));

    LOG.info("请求参数验证失败，field: {}, value: {}, message: {}", error.getField(), error.getRejectedValue(), error.getDefaultMessage());
    return container;
  }

  /**
   * Convert all errors to map.
   */
  private List<Object> error(List<FieldError> errors) {
    List<Object> errMapList = new ArrayList<>();
    errors.stream().forEach(fieldError -> {
      Map value = new ConcurrentHashMap();
      value.put("key", fieldError.getField());
      value.put("msg", fieldError.getDefaultMessage());
      errMapList.add(value);
    });
    return errMapList;
  }

  /**
   * Convert all errors to map.
   */
  private List<Object> buildErrorMsg(List<ConstraintViolation> errors) {
    List<Object> errMapList = new ArrayList<>();
    errors.stream().forEach(fieldError -> {
      Map value = new ConcurrentHashMap();
      value.put("key", getFieldName(fieldError));
      value.put("msg", fieldError.getMessage());
      errMapList.add(value);
    });
    return errMapList;
  }

  private String getFieldName(ConstraintViolation violation) {
    PathImpl propertyPath = (PathImpl) violation.getPropertyPath();

    return propertyPath.getLeafNode().getName();
  }

  private void attachErrorHeader(HttpServletResponse response) {
    response.addHeader(ExceptionHandlerConstants.RESPONSE_HEADER_ERROR_MESSAGE, "Api arguments error.");
    response.addHeader("Access-Control-Expose-Headers", ExceptionHandlerConstants.RESPONSE_HEADER_ERROR_MESSAGE);
  }
}
