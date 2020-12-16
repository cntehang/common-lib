package com.tehang.common.utility.modelconversion;

import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.Converter;

import javax.annotation.PostConstruct;

/**
 * 定义一个特有逻辑的 Converter
 * 注意需要添加 `@Component` 注解以实现 AutoRegister 的功能
 *
 * @param <S> 转换源类型
 * @param <T> 转换目标类型
 */
public abstract class AutoRegisterModelConverter<S, T> implements Converter<S, T> {
  @Autowired
  private ModelConversionService modelConversionService;

  @PostConstruct
  public void afterConstruct() {
    modelConversionService.registerConverter(this);
  }

  @Override
  public T convert(MappingContext<S, T> context) {
    return convert(context.getSource());
  }

  abstract public T convert(S source);
}
