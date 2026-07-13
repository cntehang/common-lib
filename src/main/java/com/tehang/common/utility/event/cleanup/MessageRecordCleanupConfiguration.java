package com.tehang.common.utility.event.cleanup;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 消息记录清理组件配置.
 */
@Configuration
@ConditionalOnProperty(prefix = "tehang.message.cleanup", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MessageRecordCleanupProperties.class)
@Import({
    MessageRecordCleanupJdbcRepository.class,
    MessageRecordCleanupService.class,
    MessageRecordCleanupTask.class
})
public class MessageRecordCleanupConfiguration {

}
