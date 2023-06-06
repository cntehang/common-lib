package com.tehang.common.utility.generator;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花算法 ID 生成器相关的配置.
 */
@Data
@Configuration
public class IdGeneratorConfig {

  /**
   * 数据中心编号.
   */
  @Value("${tehang.idGen.dataCenterId:1}")
  private int dataCenterId;

  /**
   * 机器编号.
   */
  @Value("${tehang.idGen.machineId:1}")
  private int machineId;

}
