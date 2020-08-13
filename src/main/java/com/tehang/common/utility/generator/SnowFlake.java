package com.tehang.common.utility.generator;


import com.tehang.common.infrastructure.exceptions.SystemErrorException;

/**
 * 雪花算法，用于生成long型ID
 * ID为64位Long。 从高位算起，第一部分为 毫米时间戳， 占 64 - 22 = 42 位。其次数据中心5位， 机器标识5位，最后同一毫秒内的序列号12位。
 */
public class SnowFlake {

  /**
   * 起始的时间戳 2018-08-20
   */
  private static final long START_STMP = 1534694400000L;

  /**
   * 每一部分占用的位数
   */
  private static final long SEQUENCE_BIT = 12; //序列号占用的位数
  private static final long MACHINE_BIT = 5;   //机器标识占用的位数
  private static final long DATACENTER_BIT = 5;//数据中心占用的位数

  /**
   * 每一部分的最大值
   */
  private static final long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
  private static final long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
  private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

  /**
   * 每一部分向左的位移
   */
  private static final long MACHINE_LEFT = SEQUENCE_BIT;
  private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
  private static final long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
  private static final int ONE = 1;

  private long datacenterId;  //数据中心
  private long machineId;     //机器标识
  private long sequence; //序列号
  private long lastStmp = -1L;//上一次时间戳

  /**
   * constructor
   *
   * @param datacenterId
   * @param machineId
   */
  public SnowFlake(long datacenterId, long machineId) {
    if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
      throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
    }
    if (machineId > MAX_MACHINE_NUM || machineId < 0) {
      throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
    }
    this.datacenterId = datacenterId;
    this.machineId = machineId;
  }

  /**
   * 产生下一个ID
   *
   * @return
   */
  public long nextId() {
    synchronized (this) {
      long currStmp = getNewstmp();
      if (currStmp < lastStmp) {
        throw new SystemErrorException("Clock moved backwards.  Refusing to generate id");
      }

      if (currStmp == lastStmp) {
        //相同毫秒内，序列号自增
        sequence = (sequence + ONE) & MAX_SEQUENCE;
        //同一毫秒的序列数已经达到最大
        if (sequence == ONE) {
          currStmp = getNextMill();
        }
      } else {
        //不同毫秒内，序列号置为0
        sequence = 0L;
      }

      lastStmp = currStmp;
      return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
              | datacenterId << DATACENTER_LEFT       //数据中心部分
              | machineId << MACHINE_LEFT             //机器标识部分
              | sequence;                             //序列号部分
    }
  }

  private long getNextMill() {
    long mill = getNewstmp();
    while (mill <= lastStmp) {
      mill = getNewstmp();
    }
    return mill;
  }

  private long getNewstmp() {
    return System.currentTimeMillis();
  }
}
