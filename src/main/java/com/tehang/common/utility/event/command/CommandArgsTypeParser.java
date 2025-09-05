package com.tehang.common.utility.event.command;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 命令参数的类型信息的解析类。
 */
public final class CommandArgsTypeParser {

  private CommandArgsTypeParser() {
  }

  public static CommandTypeInfo parse(Command command) {
    // 递归查找command对象及其基类实现的Command接口参数
    CommandTypeInfo commandTypeInfo = getCommandTypeInfo(command);
    if (commandTypeInfo == null) {
      throw new SystemErrorException("invalid Command Type: " + command.getClass());
    }
    return commandTypeInfo;
  }

  private static CommandTypeInfo getCommandTypeInfo(Command command) {
    Class<?> clazz = command.getClass();
    while (clazz != null) {
      // 获取clazz直接实现的接口列表
      Type[] genericInterfaces = clazz.getGenericInterfaces();

      for (Type genericInterface : genericInterfaces) {
        if (genericInterface instanceof ParameterizedType) {
          var parameterizedType = (ParameterizedType) genericInterface;
          if (parameterizedType.getRawType() == Command.class) {
            // 找到该clazz实现的Command泛型接口
            return getCommandTypeInfo(parameterizedType);
          }
        }
      }
      // 继续查找基类的接口
      clazz = clazz.getSuperclass();
    }
    return null;
  }

  private static CommandTypeInfo getCommandTypeInfo(ParameterizedType commandParameterizedType) {
    Type[] actualTypeArguments = commandParameterizedType.getActualTypeArguments();

    // 处理第一个类型参数 (T)
    Class<?> argType;
    if (actualTypeArguments[0] instanceof ParameterizedType) {
      // 如果是参数化类型（如 List<Long>），提取原始类型
      ParameterizedType paramType = (ParameterizedType) actualTypeArguments[0];
      argType = (Class<?>) paramType.getRawType();
    }
    else {
      // 如果是普通类，直接转换
      argType = (Class<?>) actualTypeArguments[0];
    }

    // 处理第二个类型参数 (R)
    Class<?> returnType;
    if (actualTypeArguments[1] instanceof ParameterizedType) {
      ParameterizedType paramType = (ParameterizedType) actualTypeArguments[1];
      returnType = (Class<?>) paramType.getRawType();
    }
    else {
      returnType = (Class<?>) actualTypeArguments[1];
    }

    return CommandTypeInfo.of(argType, returnType);
  }
}
