package com.tehang.common.utility.db.jpa;

import com.tehang.common.infrastructure.exceptions.NotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Tmc仓储的基础接口.
 */
@NoRepositoryBean
public interface BaseRepository<T, K> extends JpaRepository<T, K>, JpaSpecificationExecutor<T> {

  Logger LOG = LoggerFactory.getLogger(BaseRepository.class);

  /**
   * 获取上下文中的EntityManager.
   */
  EntityManager getEntityManager();

  /**
   * 根据id获取对象，如果对象不存在，将抛出NotExistException异常.
   */
  default T findByIdEnsured(K id) {
    return findById(id).orElseThrow(() -> new NotExistException(String.format("查找的 %s 记录[id: %s]不存在", getTypeName(), id)));
  }

  /**
   * 根据id获取对象，如果对象不存在，返回null.
   */
  default T findByIdOrNull(K id) {
    T result = null;
    if (id != null) {
      result = findById(id).orElse(null);
    }
    return result;
  }

  /**
   * 根据id获取对象，并加上指定的锁类型，如果对象不存在，将抛出NotExistException异常.
   */
  default T findByIdEnsuredWithLock(Class<T> entityClass, K id, LockModeType lockModeType) {
    T entity = getEntityManager().find(entityClass, id, lockModeType);
    if (entity == null) {
      throw new NotExistException(String.format("查找的 %s 记录[id: %s]不存在", getTypeName(), id));
    }
    return entity;
  }

  /**
   * 根据对象ID，并加上指定的锁类型，查找对象，并返回Optional对象.
   */
  default Optional<T> findByIdWithLock(Class<T> entityClass, K id, LockModeType lockModeType) {
    T entity = getEntityManager().find(entityClass, id, lockModeType);
    if (entity != null) {
      return Optional.of(entity);
    }
    else {
      return Optional.empty();
    }
  }

  /**
   * 根据id获取对象，并加上悲观写锁，如果对象不存在，将抛出NotExistException异常.
   */
  default T findByIdEnsuredWithWriteLock(Class<T> entityClass, K id) {
    return findByIdEnsuredWithLock(entityClass, id, LockModeType.PESSIMISTIC_WRITE);
  }

  /**
   * 为对象加上指定的锁类型.
   */
  default void lock(Object entity, LockModeType lockMode) {
    getEntityManager().lock(entity, lockMode);
  }

  /**
   * 获取泛型类型名称.
   */
  default String getTypeName() {
    try {
      // 默认 Interfaces 都取第一个，后续观察是否有其它情况
      final Type tType = ((ParameterizedType) this.getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
      try {
        return ((Class<?>) tType).getSimpleName();
      }
      catch (RuntimeException exception) {
        LOG.error("Repository {} T-Type {} cast to Class seems failed: {}", this.getClass().getName(), tType.getTypeName(), exception.getMessage(),
          exception);
        return tType.getTypeName();
      }
    }
    catch (RuntimeException exception) {
      LOG.error("Repository {} getTypeName failed: {}", this.getClass().getName(), exception.getMessage(), exception);
      return "";
    }
  }
}
