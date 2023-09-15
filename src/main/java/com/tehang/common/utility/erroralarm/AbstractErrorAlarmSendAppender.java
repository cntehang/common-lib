package com.tehang.common.utility.erroralarm;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lombok.Getter;
import lombok.Setter;

import static org.apache.commons.lang3.StringUtils.startsWith;

/**
 * 线上告警发送者抽象类.
 */
@Getter
@Setter
public abstract class AbstractErrorAlarmSendAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  private static final String ACTIVE_ENV = "prd";
  private static final String TE_HANG_LOGGER_NAME_PREFIX = "com.tehang";

  Layout<ILoggingEvent> layout;

  String notifyUrl;

  @Override
  public void start() {
    if (layout == null) {
      super.addError("获取layout标签失败");
      return;
    }

    super.start();
  }

  @Override
  public void stop() {
    if (!isStarted()) {
      return;
    }

    super.stop();
  }

  @Override
  protected void append(ILoggingEvent event) {
    if (event == null || !isStarted()) {
      return;
    }

    var active = context.getProperty("active");
    var level = event.getLevel();
    var loggerName = event.getLoggerName();

    if (isNeededAlarm(active, level, loggerName)) {
      var alarmMessage = layout.doLayout(event);
      sendAlarmMessage(alarmMessage, notifyUrl);
    }
  }

  private static boolean isNeededAlarm(String activeEnv, Level level, String loggerName) {
    return isNeededAlarmEnv(activeEnv)
        && isNeededAlarmLevel(level)
        && isNeededAlarmLoggerName(loggerName);
  }

  private static boolean isNeededAlarmEnv(String activeEnv) {
    return startsWith(activeEnv, ACTIVE_ENV);
  }

  private static boolean isNeededAlarmLevel(Level level) {
    return level.isGreaterOrEqual(Level.ERROR);
  }

  private static boolean isNeededAlarmLoggerName(String loggerName) {
    return startsWith(loggerName, TE_HANG_LOGGER_NAME_PREFIX);
  }

  /**
   * 发送告警.
   */
  protected abstract void sendAlarmMessage(String alarmMessage, String notifyUrl);
}
