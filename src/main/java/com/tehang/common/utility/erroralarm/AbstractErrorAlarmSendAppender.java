package com.tehang.common.utility.erroralarm;

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

  private static final String ACTIVE_ENV = "dev";

  Layout<ILoggingEvent> layout;

  String notifyUrl;

  @Override
  public void start() {
    if (layout == null) {
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
    if (startsWith(active, ACTIVE_ENV)) {
      var alarmMessage = layout.doLayout(event);
      sendAlarmMessage(alarmMessage, notifyUrl);
    }
  }

  /**
   * 发送告警.
   */
  protected abstract void sendAlarmMessage(String alarmMessage, String notifyUrl);
}
