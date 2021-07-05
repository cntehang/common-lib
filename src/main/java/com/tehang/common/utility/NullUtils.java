package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NullUtils {

  public static <T> T getOrNull(Supplier<T> getMethod) {
    return getOrElse(getMethod, null);
  }

  public static <T> T getOrElse(Supplier<T> getMethod, T defaultValue) {
    return getOrElseGet(getMethod, (ex) -> defaultValue);
  }

  public static <T> T getOrElseGet(Supplier<T> getMethod, @NotNull Function<NullPointerException, T> defaultGetMethod) {
    try {
      return getMethod.get();
    }
    catch (NullPointerException exception) {
      return defaultGetMethod.apply(exception);
    }
  }
}
