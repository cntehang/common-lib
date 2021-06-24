package com.tehang.common.utility.db.jpa;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Supplier;

/**
 * jpa事务使用的辅助方法
 */
@Service
@SuppressWarnings("PMD.DoNotUseThreads")
public class TransactionHelper {
  /**
   * 将调用的方法包装在事务中，并返回指定类型的参数
   *
   * @param supplier
   * @param <T>
   * @return
   */
  @Transactional
  public <T> T withTransaction(Supplier<T> supplier) {
    return supplier.get();
  }

  /**
   * 将调用的方法包装在事务中，无返回参数
   *
   * @param runnable
   */
  @Transactional
  public void withTransaction(Runnable runnable) {
    runnable.run();
  }
  
  /**
   * 将调用的方法包装在事务中并且新开一个事务，无返回参数
   *
   * @param runnable
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void withNewTransaction(Runnable runnable) {
    runnable.run();
  }

  /**
   * 在当前的上下文事物完成之后执行，需要注意使用这个方法，内部必须没有事务，否则会在一个事务内
   */
  public static void afterTransactionCommit(Runnable runnable) {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
      @Override
      public void afterCommit() {
        //在当前事务完成之后执行给定的方法
        runnable.run();
      }
    });
  }
}
