package com.tehang.common.utility.log;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Stack;

/**
 * 业务日志上下文持有者工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BizLogContextHolder {

  private static ThreadLocal<Stack<BizLogContext>> bizLogContextThreadLocal = new ThreadLocal<>();

  /**
   * 获取当前业务日志上下文
   */
  public static BizLogContext getCurrentBizLogContext() {
    Stack<BizLogContext> stack = getStack(bizLogContextThreadLocal);

    if (stack.isEmpty()) {
      return null;
    }

    return stack.peek();
  }

  /**
   * 设置业务日志上下文
   */
  public static void setBizLogContext(BizLogContext bizLogContext) {
    getStack(bizLogContextThreadLocal).push(bizLogContext);
  }

  /**
   * 移除业务日志上下文
   */
  public static void removeBizLogContext() {
    getStack(bizLogContextThreadLocal).pop();
  }

  /**
   * 设置日志中与业务相关的信息
   * <p>
   * 由具体的服务设置此信息
   */
  public static void setBizInfo(BizInfo bizInfo) {
    BizLogContext context = getCurrentBizLogContext();

    if (context != null) {
      context.setBizInfo(bizInfo);
    }
    else {
      throw new SystemErrorException("Current BizLogContext Is Null!");
    }
  }

  private static Stack<BizLogContext> getStack(ThreadLocal<Stack<BizLogContext>> threadLocal) {
    Stack<BizLogContext> stack = threadLocal.get();

    if (stack == null) {
      stack = new Stack<>();
      threadLocal.set(stack);
    }

    return stack;
  }
}
