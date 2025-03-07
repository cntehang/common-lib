package com.tehang.common.utility.redis;

import com.tehang.common.utility.time.BjTime;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 操作redis的类.
 */
@Component
@AllArgsConstructor
public class CommonRedisOperator {

  private final StringRedisTemplate stringRedisTemplate;

  /**
   * 向 redis 中插入一个 key-value 对.
   *
   * @param key   键
   * @param value 值
   */
  public void set(String key, String value) {
    stringRedisTemplate.opsForValue().set(key, value);
  }

  /**
   * 向 redis 中插入一个 key-value 对.
   *
   * @param key     键
   * @param value   值
   * @param timeOut 过期时常
   * @param unit    单位
   */
  public void set(String key, String value, long timeOut, TimeUnit unit) {
    stringRedisTemplate.opsForValue().set(key, value, timeOut, unit);
  }

  /**
   * 从 redis 中获取一个值.
   *
   * @param key 键
   * @return 值
   */
  public String get(String key) {
    return stringRedisTemplate.opsForValue().get(key);
  }

  /**
   * 当redis中未找到相应的key时，向redis中设置值，并返回true。 否则不做任何操作，并返回false.
   */
  public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
    return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
  }

  /** 重设指定Key的过期时间。*/
  public void expires(String key, long timeout, TimeUnit unit) {
    stringRedisTemplate.expire(key, timeout, unit);
  }

  /** 重设指定Key的过期时间。*/
  public void expireAt(String key, BjTime bjTime) {
    stringRedisTemplate.expireAt(key, bjTime.getInnerTime().toDate());
  }

  /**
   * 删除指定的key, 如果删除成功，返回true, 否则返回false.
   */
  public Boolean delete(String key) {
    return stringRedisTemplate.delete(key);
  }

  /**
   * Executes the given {@link RedisScript}
   *
   * @param script The script to execute
   * @param keys Any keys that need to be passed to the script
   * @param args Any args that need to be passed to the script
   * @return The return value of the script or null if {@link RedisScript#getResultType()} is null, likely indicating a
   *         throw-away status reply (i.e. "OK")
   */
  @Nullable
  public <T> T execute(RedisScript<T> script, List<String> keys, Object... args) {
    return stringRedisTemplate.execute(script, keys, args);
  }
}
