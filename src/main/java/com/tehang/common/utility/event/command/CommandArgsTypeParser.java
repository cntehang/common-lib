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
    // 获取command的直接实现接口
    Type[] genericInterfaces = command.getClass().getGenericInterfaces();

    for (Type genericInterface : genericInterfaces) {
      if (genericInterface instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        return CommandTypeInfo.of(actualTypeArguments[0].getClass(), actualTypeArguments[1].getClass());
      }
    }

    throw new SystemErrorException("invalid Command Type: " + command.getClass());
  }
}
