package com.tehang.common.utility.validation;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.messageinterpolation.util.InterpolationHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * 自定义集合元素校验器.
 */
public class StringListPattern implements ConstraintValidator<StringListValidator, List<String>> {

  private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

  private java.util.regex.Pattern pattern;
  private String escapedRegexp;

  /**
   * 初始化方法，在校验器实例化时调用.
   */
  @Override
  public void initialize(StringListValidator parameters) {
    try {
      pattern = java.util.regex.Pattern.compile(parameters.regexp());
    } catch (PatternSyntaxException e) {
      throw LOG.getInvalidRegularExpressionException(e);
    }

    escapedRegexp = InterpolationHelper.escapeMessageParameter(parameters.regexp());
  }

  /**
   * 核心校验方法，判断集合中所有元素是否符合正则表达式规则.
   */
  @Override
  public boolean isValid(List<String> objects, ConstraintValidatorContext context) {
    if (CollectionUtils.isEmpty(objects)) {
      return true;
    }

    if (context instanceof HibernateConstraintValidatorContext) {
      context.unwrap(HibernateConstraintValidatorContext.class).addMessageParameter("regexp", escapedRegexp);
    }

    return objects.stream().allMatch(nef -> pattern.matcher(nef).matches());
  }
}
