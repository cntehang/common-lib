package com.tehang.common.infrastructure.exceptions.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tehang.common.infrastructure.exceptions.ApplicationException;
import com.tehang.common.infrastructure.exceptions.BusinessException;
import com.tehang.common.infrastructure.exceptions.ParameterException;
import com.tehang.common.utility.api.CommonCode;
import com.tehang.common.utility.api.front.DataContainer;
import com.tehang.common.utility.consts.ExceptionHandlerConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 处理系统自定义错误的地方.
 */
@Component
public class CommonExceptionHandler implements HandlerExceptionResolver {

  private static final int ERROR_MESSAGE_END_INDEX = 200;

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  /**
   * 业务异常处理.
   *
   * @param request
   * @param response
   * @param handler
   * @param ex
   * @return
   */
  @Override
  public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    Logger logger = getLogger(handler);
    resolve(response, ex, logger);
    return new ModelAndView();
  }

  private Logger getLogger(Object handler) {
    Logger logger = LOG;
    if (handler instanceof HandlerMethod) {
      HandlerMethod handlerMethod = (HandlerMethod) handler;
      logger = LoggerFactory.getLogger(handlerMethod.getBeanType());
    }
    return logger;
  }

  private static void resolve(HttpServletResponse response, Exception ex, Logger logger) {
    // error message header 用于在filter中判断请求响应是否出现异常
    String exceptionMessage = StringUtils.substring(ex.getMessage(), 0, ERROR_MESSAGE_END_INDEX);
    response.addHeader(ExceptionHandlerConstants.RESPONSE_HEADER_ERROR_MESSAGE, exceptionMessage);
    response.addHeader("Access-Control-Expose-Headers", ExceptionHandlerConstants.RESPONSE_HEADER_ERROR_MESSAGE);

    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json; charset=utf-8");

    if (ex instanceof ApplicationException) {
      // 内部逻辑层抛出的异常: 因为内部逻辑抛出异常之前已经记录日志，这里只记录trace级别
      logger.trace("ApplicationException happen: {}", ex.getMessage(), ex);
      ApplicationException exception = (ApplicationException) ex;
      writeResponse(response, getCommonData(exception.getCode(), exception.getMessage()));

    } else if (ex instanceof ParameterException || ex instanceof ConstraintViolationException) {
      // 参数校验抛出的异常: 记录warn级别日志
      logger.warn("ParameterException or ConstraintViolationException happen: {}", ex.getMessage(), ex);
      writeResponse(response, getCommonData(CommonCode.PARAMETER_ERROR_CODE, ex.getMessage()));

    } else if (ex instanceof BusinessException) {
      // 业务层抛出的异常: 记录warn级别日志
      logger.warn("BusinessException happen: {}", ex.getMessage(), ex);
      writeResponse(response, getOtherData(ex));

    } else if (ex instanceof DataAccessException) {
      // 数据持久层抛出的异常: 记录错误日志
      logger.error("DataAccessException happen: {}", ex.getMessage(), ex);
      writeResponse(response, getDataBaseData(ex));
    } else {
      //对于这种意料之外的异常，在使用中有两种情况：
      //1. 代码中没考虑到会出错的地方，这种是代码写错了，比如数组越界了之类的
      //2. 出错了也无法在代码层次补救的地方，如基础数据获取不全造成的业务处理错误（获取不到某个保险code对应的保险信息等）
      logger.error("Unknown Exception happen: {}", ex.getMessage(), ex);
      writeResponse(response, getOtherData(ex));
    }
  }

  private static void writeResponse(HttpServletResponse response, DataContainer data) {
    PrintWriter out = null;
    try {
      out = response.getWriter();
      out.append(new ObjectMapper().writeValueAsString(data));

    } catch (IOException exx) {
      LOG.error("Catch IOException: exx = []", exx);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

  private static DataContainer getCommonData(int code, String message) {
    return new DataContainer(code, message);
  }

  private static DataContainer getDataBaseData(Exception ex) {
    DataContainer data = new DataContainer(CommonCode.SQL_ERROR_CODE, CommonCode.SQL_ERROR_MESSAGE);
    String sqlDebugMsg = Arrays.stream(ex.getStackTrace())
        .map(StackTraceElement::toString)
        .collect(Collectors.joining(";"));
    data.setDebugMsg(sqlDebugMsg);
    return data;
  }

  private static DataContainer getOtherData(Exception ex) {
    DataContainer data = new DataContainer(CommonCode.COMMON_ERROR_CODE, StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : ex.toString());
    String debugMsg = Arrays.stream(ex.getStackTrace())
        .map(StackTraceElement::toString)
        .filter(msg -> msg.contains("com.tehang"))
        .collect(Collectors.joining(";"));
    data.setDebugMsg(debugMsg);
    return data;
  }
}
