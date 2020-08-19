package com.tehang.common.utility.consts;

import com.tehang.common.infrastructure.exceptions.ParameterException;

import java.util.function.Supplier;

public final class FunctionalHelper {

  private FunctionalHelper() {
  }

  public static Runnable createExceptionRunnable(String message) {
    return () -> {
      throw new ParameterException(message);
    };
  }

  public static Supplier<? extends RuntimeException> createExceptionSupplier(String message) {
    return () -> new ParameterException(message);
  }

}
