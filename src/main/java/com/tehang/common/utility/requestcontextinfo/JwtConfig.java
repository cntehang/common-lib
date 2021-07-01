package com.tehang.common.utility.requestcontextinfo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * jwt 相关配置
 */
@Configuration
@Data
@ConfigurationProperties("tehang.inner.auth")
public class JwtConfig {

  /**
   * 生成以及解密jwt所需要的密钥
   */
  private String jwtSecret;
}
