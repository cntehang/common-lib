package com.tehang.common.utility.generator;


import java.util.UUID;

/**
 * 数据库主键id生成器：通过SnowFlake算法生成一个long型的主键
 */
public final class IdGenerator {

  private static SnowFlake snowFlake;

  private IdGenerator() {
    // do nothing
  }

  /**
   * 用于初始化实例。
   *
   * @param dataCenterId
   * @param machineId
   */
  public static void initInstance(long dataCenterId, long machineId) {
    snowFlake = new SnowFlake(dataCenterId, machineId);
  }

  /**
   * 产生一个新的id
   *
   * @return
   */
  public static long nextId() {
    return snowFlake.nextId();
  }

  /**
   * 产生一个新的uuid的字符串形式
   *
   * @return
   */
  public static String nextUuid() {
    return UUID.randomUUID().toString();
  }

}
