package com.tehang.common.utility.validation;

import com.tehang.common.infrastructure.exceptions.ParameterException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidateUtils {
  private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  public static <T> ValidateResult<T> validateObject(T obj) {
    return validateObject(obj, null);
  }

  public static <T> ValidateResult<T> validateObject(T obj, Class<?> group) {
    Set<ConstraintViolation<T>> violations;
    if (group == null) {
      violations = validator.validate(obj, Default.class);
    }
    else {
      violations = validator.validate(obj, Default.class, group);
    }
    return new ValidateResult<>(violations);
  }

  public static <T> void validateObjectAndThrowException(T obj) throws ParameterException {
    validateObjectAndThrowException(obj, null);
  }

  public static <T> void validateObjectAndThrowException(T obj, Class<?> group) throws ParameterException {
    final ValidateResult<T> validateResult = validateObject(obj, group);

    if (!validateResult.isValid()) {
      throw new ParameterException(validateResult.getDescription());
    }
  }

  public static <T, E extends Throwable> void validateObjectAndThrowMappedException(T obj, Function<ParameterException, E> exceptionMapping) throws E {
    try {
      validateObjectAndThrowException(obj);
    }
    catch (ParameterException ex) {
      throw exceptionMapping.apply(ex);
    }
  }
}
