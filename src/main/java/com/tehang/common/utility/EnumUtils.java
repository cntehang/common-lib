package com.tehang.common.utility;

import javax.validation.constraints.NotNull;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 枚举类型的辅助类。
 */
public final class EnumUtils {

  private EnumUtils() {

  }

  /**
   * 检查传入的枚举常量是否有效(区分大小写)。
   * 传入null或者无效的枚举常量，将返回false。
   */
  public static <E extends Enum<E>> boolean isValidEnum(@NotNull final Class<E> enumClass, final String enumName) {
    return org.apache.commons.lang3.EnumUtils.isValidEnum(enumClass, enumName);
  }

  /**
   * 根据传入的枚举常量，获取枚举项。传入null或无效的枚举常量，将返回null。
   */
  public static <E extends Enum<E>> E getEnum(@NotNull final Class<E> enumClass, final String enumName) {
    return org.apache.commons.lang3.EnumUtils.getEnum(enumClass, enumName);
  }

  /**
   * 根据传入的枚举常量，获取枚举项的描述信息。
   * 如果该枚举实现了Describable接口，则返回该枚举的description值，否则返回定义的枚举常量。
   * 如果传入的枚举常量无效，则返回null。
   */
  public static <E extends Enum<E>> String getEnumDescription(@NotNull final Class<E> enumClass, final String enumName) {
    var enumItem = getEnum(enumClass, enumName);
    if (enumItem == null) {
      return null;
    }
    return getEnumItemDescription(enumItem);
  }

  /**
   * 获取枚举类型的枚举项的列表，返回的列表是可变的。
   */
  @NotNull
  public static <E extends Enum<E>> List<E> getEnumList(@NotNull final Class<E> enumClass) {
    return org.apache.commons.lang3.EnumUtils.getEnumList(enumClass);
  }

  /**
   * 解析枚举类型，得到对应的字典数据。
   * 如果枚举实现了Describable接口，则根据该接口获取描述信息，否则code和描述信息均为定义的枚举常量。
   */
  @NotNull
  public static <E extends Enum<E>> List<DictItemDto> getDictItems(@NotNull final Class<E> enumClass) {
    return getEnumList(enumClass).stream()
        .map(EnumUtils::getDictItem)
        .collect(toList());
  }

  private static <E extends Enum<E>> DictItemDto getDictItem(E enumItem) {
    return DictItemDto.of(enumItem.name(), getEnumItemDescription(enumItem));
  }

  /** 获取枚举项的描述信息。*/
  private static <E extends Enum<E>> String getEnumItemDescription(E enumItem) {
    if (enumItem instanceof Describable) {
      return ((Describable) enumItem).getDescription();
    }
    else {
      return enumItem.name();
    }
  }
}
