package com.tehang.common.utility

import com.tehang.common.TestSpecification
import com.tehang.common.utility.event.publish.eventrecord.DomainEventSendStatus
import com.tehang.common.utility.time.TimeIntervalType

class EnumUtilsSpec extends TestSpecification {

  def "test isValidEnum"() {
    expect:
    EnumUtils.isValidEnum(DomainEventSendStatus.class, DomainEventSendStatus.WaitSend.toString())
    !EnumUtils.isValidEnum(DomainEventSendStatus.class, null)
    !EnumUtils.isValidEnum(DomainEventSendStatus.class, "xxx")
  }

  def "test getEnum"() {
    expect:
    EnumUtils.getEnum(DomainEventSendStatus.class, DomainEventSendStatus.WaitSend.toString()) == DomainEventSendStatus.WaitSend
    EnumUtils.getEnum(DomainEventSendStatus.class, null) == null
    EnumUtils.getEnum(DomainEventSendStatus.class, "xxx") == null
  }

  def "test getEnumDescription"() {
    expect:
    // 如果该枚举实现了Describable接口，则返回该枚举的description值
    EnumUtils.getEnumDescription(DomainEventSendStatus.class, DomainEventSendStatus.WaitSend.toString()) == DomainEventSendStatus.WaitSend.getDescription()
    EnumUtils.getEnumDescription(DomainEventSendStatus.class, null) == null
    EnumUtils.getEnumDescription(DomainEventSendStatus.class, "xxx") == null

    // 否则返回定义的枚举常量
    EnumUtils.getEnumDescription(TimeIntervalType.class, TimeIntervalType.Day.toString()) == TimeIntervalType.Day.toString()
  }

  def "test getEnumList"() {
    expect:
    EnumUtils.getEnumList(DomainEventSendStatus.class) == List.of(
        DomainEventSendStatus.WaitSend,
        DomainEventSendStatus.SendSuccess,
        DomainEventSendStatus.SendFailed)

    EnumUtils.getEnumList(TimeIntervalType.class) == List.of(
        TimeIntervalType.Day,
        TimeIntervalType.Hour,
        TimeIntervalType.Minute,
        TimeIntervalType.Second)
  }

  def "test getDictItems"() {
    expect:
    EnumUtils.getDictItems(DomainEventSendStatus.class) == List.of(
        DictItemDto.of(DomainEventSendStatus.WaitSend.name(), DomainEventSendStatus.WaitSend.getDescription()),
        DictItemDto.of(DomainEventSendStatus.SendSuccess.name(), DomainEventSendStatus.SendSuccess.getDescription()),
        DictItemDto.of(DomainEventSendStatus.SendFailed.name(), DomainEventSendStatus.SendFailed.getDescription()))

    EnumUtils.getDictItems(TimeIntervalType.class) == List.of(
        DictItemDto.of(TimeIntervalType.Day.name(), TimeIntervalType.Day.name()),
        DictItemDto.of(TimeIntervalType.Hour.name(), TimeIntervalType.Hour.name()),
        DictItemDto.of(TimeIntervalType.Minute.name(), TimeIntervalType.Minute.name()),
        DictItemDto.of(TimeIntervalType.Second.name(), TimeIntervalType.Second.name()))
  }
}
