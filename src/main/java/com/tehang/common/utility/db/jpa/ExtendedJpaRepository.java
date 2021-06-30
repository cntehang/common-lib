package com.tehang.common.utility.db.jpa;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;

/**
 * ExtendedJpaRepository.
 *
 * @author Oliver Gierke
 * @soundtrack Elen - Nobody Else (Elen)
 */
public class ExtendedJpaRepository<T, K> extends SimpleJpaRepository<T, K> implements BaseRepository<T, K> {

  private EntityManager entityManager;

  /**
   * Creates a new {@link ExtendedJpaRepository} for the given {@link JpaEntityInformation} and {@link EntityManager}.
   *
   * @param entityInformation must not be {@literal null}.
   * @param entityManager     must not be {@literal null}.
   */
  public ExtendedJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }

  @Override
  public EntityManager getEntityManager() {
    return entityManager;
  }
}
