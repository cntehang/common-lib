package com.tehang.common.utility.db.jpa;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

/**
 * 打开jpa会话的Aspect类
 */
@Aspect
@Component
public class OpenJpaSessionAspect implements ApplicationContextAware {

  private static final Logger LOG = LoggerFactory.getLogger(OpenJpaSessionAspect.class);


  private ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    this.context = context;
  }

  /**
   * 通过around方式，在方法执行前打开Session, 在方法执行后关闭Session
   *
   * @param joinPoint
   * @return
   * @throws Throwable
   */
  @Around("@annotation(com.tehang.tmc.staff.utility.jpa.OpenJpaSession)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

    boolean wasOpened;
    EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);
    if (TransactionSynchronizationManager.hasResource(emf)) {
      //当前已经开启了session, 这里不再开启
      wasOpened = false;

    } else {
      LOG.debug("Opening JPA EntityManager in OpenJpaSessionAspect");
      try {
        EntityManager em = emf.createEntityManager();
        EntityManagerHolder emHolder = new EntityManagerHolder(em);
        TransactionSynchronizationManager.bindResource(emf, emHolder);
        wasOpened = true;

      } catch (PersistenceException ex) {
        LOG.warn("create JPA EntityManager error, {}", ex.getMessage(), ex);
        throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
      }
    }

    try {
      return joinPoint.proceed();

    } finally {
      if (wasOpened) {
        EntityManagerHolder emHolder = (EntityManagerHolder)TransactionSynchronizationManager.unbindResource(emf);
        LOG.debug("Closing JPA EntityManager in OpenJpaSessionAspect");
        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
      }
    }
  }

}