package com.tehang.common.utility.generator;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * 数据库主键id生成器：通过SnowFlake算法生成一个long型的主键.
 */
@Component
public final class IdGenerator {

  private static SnowFlake snowFlake;

  private IdGeneratorConfig idGeneratorConfig;

  /**
   * DI.
   */
  public IdGenerator(IdGeneratorConfig idGeneratorConfig) {
    this.idGeneratorConfig = idGeneratorConfig;
  }

  /**
   * 初始化方法.
   */
  @PostConstruct
  public void init() {
    int dataCenterId = idGeneratorConfig.getDataCenterId();
    int machineId = idGeneratorConfig.getMachineId();
    snowFlake = new SnowFlake(dataCenterId, machineId);
  }

  /**
   * 产生一个新的id.
   */
  public static long nextId() {
    return snowFlake.nextId();
  }

  /**
   * 产生一个新的uuid的字符串形式.
   */
  public static String nextUuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * 通过雪花算法获取一个新的id，推荐调用此实例方法，以方便单元测试.
   */
  public long newId() {
    return snowFlake.nextId();
  }
}
