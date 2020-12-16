package com.tehang.common.utility.modelconversion;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/**
 * 进行 Model 转换
 * 能够自动对同名属性进行映射（更多实现细节查看 http://modelmapper.org）
 * 如果有特殊的转换需求，通过自定义 `AutoRegisterModelConverter` 以实现
 */
@Slf4j
@Service
public class ModelConversionService {
  private final ModelMapper modelMapper = new ModelMapper();

  public <T> T convert(@Nullable Object source, Class<T> targetType) {
    if (source == null) {
      return null;
    }
    else {
      return modelMapper.map(source, targetType);
    }
  }

  <S, D> void registerConverter(Converter<S, D> converter) {
    modelMapper.addConverter(converter);
  }
}
