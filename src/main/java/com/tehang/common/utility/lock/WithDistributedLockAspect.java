package com.tehang.common.utility.lock;

import com.tehang.common.utility.EncryptUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class WithDistributedLockAspect {

  private final DistributedLockFactory lockFactory;

  @Around("@annotation(com.tehang.common.utility.lock.WithDistributedLock)")
  public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
    log.debug("Enter WithDistributedLockAspect.doAround");

    // 获取方法对象
    var method = ((MethodSignature) joinPoint.getSignature()).getMethod();

    // 获取annotation对象
    var lockAnnotation = method.getAnnotation(WithDistributedLock.class);

    // 获取lockKey
    String lockKey = getLockKey(joinPoint, method, lockAnnotation.keyPieces());
    log.debug("WithDistributedLockAspect.lockKey: {}", lockKey);

    // 加上分布式锁
    try (var ignored = lockFactory.acquireLock(
            lockKey,
            lockAnnotation.blocked(),
            lockAnnotation.expiredSeconds() * 1000)) {
      // 在锁范围内执行目标方法
      return joinPoint.proceed();
    }
  }

  /**
   * 获取lockKey: 根据包名，类名，方法名及key片段，拼接出lockKey
   */
  private static String getLockKey(ProceedingJoinPoint joinPoint, Method method, String[] keyPieces) {
    // lockKey = 包名 + 类名 + 方法名 + keyPieces
    var sb = new StringBuilder();
    sb.append(method.getDeclaringClass().getCanonicalName());
    sb.append(method.getName());
    if (keyPieces != null && keyPieces.length > 0) {
      for (var item : keyPieces) {
        sb.append(getKeyPieceValue(joinPoint, item));
      }
    }

    var lockKey = sb.toString();

    if (lockKey.length() > 256) {
      // 当获取的key太长时，为提高性能，进行md5处理
      lockKey = EncryptUtils.md5(lockKey);
    }
    return lockKey;
  }

  private static String getKeyPieceValue(ProceedingJoinPoint joinPoint, String keyPiece) {
    if (keyPiece.contains("#")) {
      return parseKeyPieceExpression(joinPoint, keyPiece);
    }
    else {
      return keyPiece;
    }
  }

  private static String parseKeyPieceExpression(ProceedingJoinPoint joinPoint, String keyPiece) {
    // 获取方法参数值
    Map<String, Object> paramValues = getParamValues(joinPoint);

    // 解析keyPiece表达式的值
    String expression = keyPiece.replace("#", "");
    StringTokenizer tokenizer = new StringTokenizer(expression, ".");

    Object object = null;
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (object == null) {
        // 第一个属性值从方法参数中获取
        object = paramValues.get(token);
      }
      else {
        // 解析后续的属性表达式
        object = getPropValue(object, token);
      }

      if (object == null) {
        // 如果取到了null值，就不再继续往后解析，以免出现NPE
        break;
      }
    }
    return String.valueOf(object);
  }

  /**
   * 获取参数Map集合
   */
  private static Map<String, Object> getParamValues(ProceedingJoinPoint joinPoint) {
    Map<String, Object> param = new HashMap<>();

    Object[] paramValues = joinPoint.getArgs();
    String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();

    for (int i = 0; i < paramNames.length; i++) {
      param.put(paramNames[i], paramValues[i]);
    }
    return param;
  }

  /**
   * 获取指定参数名的参数值
   */
  @SneakyThrows
  private static Object getPropValue(Object obj, String propName) {
    Field[] fields = obj.getClass().getDeclaredFields();
    for (Field f : fields) {
      if (f.getName().equals(propName)) {
        //在反射时能访问私有变量
        f.setAccessible(true);
        return f.get(obj);
      }
    }
    return null;
  }
}