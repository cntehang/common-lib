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
  private static final long START_TIMESTAMP = 1534694400000L;

  /**
   * 每一部分占用的位数
   */
  private static final long SEQUENCE_BIT = 12; //序列号占用的位数
  private static final long MACHINE_BIT = 5;   //机器标识占用的位数
  private static final long DATA_CENTER_BIT = 5;//数据中心占用的位数

  /**
   * 每一部分的最大值
   */
  private static final long MAX_DATA_CENTER_NUM = (1L << DATA_CENTER_BIT) - 1;
  private static final long MAX_MACHINE_NUM = (1L << MACHINE_BIT) - 1;
  private static final long MAX_SEQUENCE = (1L << SEQUENCE_BIT) - 1;

  /**
   * 每一部分向左的位移
   */
  private static final long MACHINE_LEFT = SEQUENCE_BIT;
  private static final long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
  private static final long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

  private final long dataCenterId;  //数据中心
  private final long machineId;     //机器标识
  private long sequence = 0L; //序列号
  private long lastTimestamp = -1L;//上一次时间戳

  /**
   * constructor
   *
   * @param dataCenterId
   * @param machineId
   */
  public SnowFlake(long dataCenterId, long machineId) {
    if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
      throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
    }
    if (machineId > MAX_MACHINE_NUM || machineId < 0) {
      throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
    }
    this.dataCenterId = dataCenterId;
    this.machineId = machineId;
  }

  /**
   * 产生下一个ID
   *
   * @return
   */
  public synchronized long nextId() {
    long curStamp = getNewTimestamp();
    if (curStamp < lastTimestamp) {
      throw new SystemErrorException("Clock moved backwards.  Refusing to generate id");
    }

    if (curStamp == lastTimestamp) {
      //相同毫秒内，序列号自增
      sequence++;
      //同一毫秒的序列数已经达到最大，则等待下一毫秒，且重制sequence
      if (sequence > MAX_SEQUENCE) {
        curStamp = getNextMill();
        sequence = 0L;
      }
    } else {
      //不同毫秒内，序列号置为0
      sequence = 0L;
    }

    lastTimestamp = curStamp;
    return (curStamp - START_TIMESTAMP) << TIMESTAMP_LEFT //时间戳部分
        | dataCenterId << DATA_CENTER_LEFT       //数据中心部分
        | machineId << MACHINE_LEFT             //机器标识部分
        | sequence;                             //序列号部分
  }

  private long getNextMill() {
    long mill = getNewTimestamp();
    while (mill <= lastTimestamp) {
      mill = getNewTimestamp();
    }
    return mill;
  }

  private long getNewTimestamp() {
    return System.currentTimeMillis();
  }

}

