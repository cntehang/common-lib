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

  private static CommandTypeInfo getCommandTypeInfo(Object obj) {
    Class<?> clazz = obj.getClass();
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
    return CommandTypeInfo.of(
        (Class<?>) actualTypeArguments[0],
        (Class<?>) actualTypeArguments[1]
    );
  }

  public static void main(String[] args) {
    var typeInfo = parse(new TestCommandBase());
    System.out.println(typeInfo.getArgsClass());
    System.out.println(typeInfo.getReturnClass());
    System.out.println(typeInfo);
  }

  static class TestCommandBase implements Command<String, Void> {

    @Override
    public String getCommandType() {
      return "MyTestCommand";
    }

    @Override
    public Void run(String args) {
      System.out.println(args);
      return null;
    }
  }

  static class TestCommand extends TestCommandBase {

  }
}
