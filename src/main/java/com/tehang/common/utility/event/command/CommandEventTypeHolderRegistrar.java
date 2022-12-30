package com.tehang.common.utility.event.command;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class CommandEventTypeHolderRegistrar implements ImportBeanDefinitionRegistrar {

  public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
    // 从EnableCommand的注解中解析出eventType
    String eventType = (String) annotationMetadata.getAnnotationAttributes(EnableCommand.class.getName()).get("eventType");
    if (isBlank(eventType)) {
      throw new SystemErrorException("commandEventType未配置");
    }
    log.info("parsed CommandEventType: {}", eventType);

    // 构造CommandEventTypeHolder, 并填充eventType字段
    BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CommandEventTypeHolder.class);
    definitionBuilder.addPropertyValue("eventType", eventType);
    beanDefinitionRegistry.registerBeanDefinition(CommandEventTypeHolder.class.getName(), definitionBuilder.getBeanDefinition());
  }
}
