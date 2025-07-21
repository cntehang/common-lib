package com.tehang.common.utility.rest;

import com.google.common.collect.Lists;
import com.tehang.common.infrastructure.exceptions.ApplicationException;
import com.tehang.common.infrastructure.exceptions.BaseException;
import com.tehang.common.infrastructure.exceptions.ParameterException;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.api.CommonCode;
import com.tehang.common.utility.api.inner.ResponseContainer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.dao.DataAccessException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 提供Controller中对返回结果进行包装的辅助方法，对引发的异常也会进行包装。
 * 使用方式:
 * ControllerResponseWrapper.wrap(() -> {
 *   return appService.getXXX();
 * }
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ControllerResponseWrapper {

  /**
   * 包装方法调用, 返回ResponseContainer<Void>, 在发生异常时也会进行包装。
   */
  public static ResponseContainer<Void> wrap(Runnable runnable) {
    return wrap(() -> {
      // 包装运行的代码
      runnable.run();
      return null;
    });
  }

  /**
   * 包装方法调用, 返回ResponseContainer<T>, 在发生异常时也会进行包装。
   */
  public static <T> ResponseContainer<T> wrap(Supplier<T> supplier) {
    try {
      T data = supplier.get();
      return new ResponseContainer(0, StringUtils.EMPTY, data);
    }
    catch (ApplicationException ex) {
      var code = ex.getCode();
      var msg = ex.getMessage();
      log.debug("ApplicationException happen, code: {}, msg: {}", code, msg, ex);
      return createResponseContainer(code, msg, ex);
    }
    catch (DataAccessException ex) {
      var code = CommonCode.SQL_ERROR_CODE;
      var msg = CommonCode.SQL_ERROR_MESSAGE + ": " + ex.getMessage();
      // 数据持久层抛出的异常, 记录error级别日志
      log.error("DataAccessException happen, code: {}, msg: {}", code, msg, ex);
      return createResponseContainer(code, msg, ex);
    }
    catch (ParameterException ex) {
      var code = CommonCode.PARAMETER_ERROR_CODE;
      var msg = ex.getMessage();
      log.debug("ParameterException happen, code: {}, msg: {}", code, msg, ex);
      return createResponseContainer(code, msg, ex);
    }
    catch (ConstraintViolationException ex) {
      List<ConstraintViolation<?>> fieldErrors = Lists.newArrayList(ex.getConstraintViolations());
      var error = fieldErrors.get(0);
      log.debug("请求参数验证失败, message: {}, invalidValue: {}", error.getMessage(), error.getInvalidValue());

      // 参数校验出错码：98
      var code = CommonCode.PARAMETER_ERROR_CODE;
      var msg = error.getMessage();
      var container = new ResponseContainer(code, msg, null);
      container.setErrors(buildErrorMsg(fieldErrors));
      return container;
    }
    catch (Exception ex) {
      var code = CommonCode.COMMON_ERROR_CODE;
      var msg = ex.getMessage();
      // 未知异常, 记录error级别日志
      log.error("Unknown Exception happen, code: {}, msg: {}", code, msg, ex);
      return createResponseContainer(code, msg, ex);
    }
  }

  private static <T> ResponseContainer<T> createResponseContainer(int code, String msg, Exception ex) {
    var container = new ResponseContainer(code, msg, null);
    container.setDebugMsg(getDebugMsg(ex));
    return container;
  }

  private static String getDebugMsg(Exception ex) {
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

  private static Map<String, String> createTrace(Exception ex) {
    Map<String, String> traceMap = new ConcurrentHashMap<>();

    if (ex != null && ex.getStackTrace() != null && ex.getStackTrace().length > 0) {
      var firstTrace = ex.getStackTrace()[0];
      String exTrace = Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).filter(msg -> msg.contains("com.tehang")).findFirst()
          .orElse(firstTrace.toString());

      if (StringUtils.isNotBlank(exTrace)) {
        traceMap.put("traceMessage", exTrace);
      }
    }
    return traceMap;
  }

  private static List<Object> buildErrorMsg(List<ConstraintViolation<?>> errors) {
    List<Object> errMapList = new ArrayList<>();
    errors.forEach(fieldError -> {
      var value = new HashMap<String, String>();
      value.put("key", getFieldName(fieldError));
      value.put("msg", fieldError.getMessage());
      errMapList.add(value);
    });
    return errMapList;
  }

  private static String getFieldName(ConstraintViolation<?> violation) {
    PathImpl propertyPath = (PathImpl) violation.getPropertyPath();
    return propertyPath.getLeafNode().getName();
  }
}
