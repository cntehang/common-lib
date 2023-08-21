package com.tehang.common.utility.erroralarm;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * 线上告警发送者抽象类.
 */
public abstract class AbstractErrorAlarmSendAppender extends AppenderBase<ILoggingEvent> {

  private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final String ACTIVE_ENV = "dev";

  @Override
  protected void append(ILoggingEvent event) {
    var active = context.getProperty("active");
    var serviceName = context.getProperty("springAppName");

    if (event.getLevel() == Level.ERROR && equalsIgnoreCase(active, ACTIVE_ENV)) {
      var alarmMessage = createAlarmMessage(event, serviceName);
      sendAlarmMessage(alarmMessage);
    }
  }

  private static AlarmMessage createAlarmMessage(ILoggingEvent event, String serviceName) {
    var alarmMessage = new AlarmMessage();

    alarmMessage.setMethodName(event.getLoggerName());
    alarmMessage.setTraceId(event.getMDCPropertyMap() == null ? null : event.getMDCPropertyMap().get("traceId"));
    alarmMessage.setServiceName(serviceName);
    alarmMessage.setErrorMessage(event.getFormattedMessage());

    var throwable = event.getThrowableProxy() == null ? null : ((ThrowableProxy)event.getThrowableProxy()).getThrowable();
    alarmMessage.setStackText(createStackText(throwable));

    synchronized (SIMPLE_DATE_FORMAT) {
      alarmMessage.setCreateTime(SIMPLE_DATE_FORMAT.format(event.getTimeStamp()));
    }

    return alarmMessage;
  }

  private static String createStackText(Throwable throwable) {
    String stack = StringUtils.EMPTY;

    if (throwable != null) {
      StringWriter stringWriter = new StringWriter();
      throwable.printStackTrace(new PrintWriter(stringWriter, true));
      stack = stringWriter.toString();
    }

    return stack;
  }

  /**
   * 发送告警.
   */
  protected abstract void sendAlarmMessage(AlarmMessage alarmMessage);
}
