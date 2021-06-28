package com.tehang.common.utility.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * web配置增强
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * 因为在所有的 HttpMessageConverter 实例集合中，StringHttpMessageConverter 要比其它的 Converter 排得靠前一些。
   * 我们需要将处理 Object 类型的 HttpMessageConverter 放得靠前一些，这可以在 Configuration 类中完成。
   * 否则 RestControllerWrapResponseAdvice 将不能正常工作。
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(0, new MappingJackson2HttpMessageConverter());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 拦截所有请求, 注册RestControllerHandlerInterceptor
    registry.addInterceptor(new RestControllerHandlerInterceptor()).addPathPatterns("/**");
  }
}
