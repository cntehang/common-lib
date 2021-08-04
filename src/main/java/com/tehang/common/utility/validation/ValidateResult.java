package com.tehang.common.utility.validation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ValidateResult<T> {
  private boolean valid;

  private Set<ConstraintViolation<T>> violations;

  private Set<String> violationDescriptions;

  private String description;

  public ValidateResult(Set<ConstraintViolation<T>> violations) {
    if (CollectionUtils.isEmpty(violations)) {
      this.valid = true;
      this.violations = SetUtils.emptySet();
      this.violationDescriptions = SetUtils.emptySet();
      this.description = StringUtils.EMPTY;
    }
    else {
      this.valid = false;
      this.violations = violations;
      this.violationDescriptions = violations.stream()
          .map(ValidateResult::formatViolation)
          .collect(Collectors.toSet());
      this.description = this.violationDescriptions.stream().findFirst().orElse(StringUtils.EMPTY);
    }
  }

  private static <T> String formatViolation(ConstraintViolation<T> violation) {
    return violation.getMessage();
  }
}
