package com.tn.service.data.jpa.repository;

import jakarta.persistence.EntityManager;

import com.tn.service.data.domain.Versioned;

public class AbstractVersionedRepository<T extends Versioned<T>>
{
  private final EntityManager entityManager;

  public AbstractVersionedRepository(EntityManager entityManager)
  {
    this.entityManager = entityManager;
  }

  protected <S extends T> S saveNextVersion(S entity)
  {
    return saveNextVersion(entityManager, entity);
  }

  static <S extends Versioned<?>> S saveNextVersion(EntityManager entityManager, S entity)
  {
    @SuppressWarnings("unchecked")
    S entityNextVersion = (S)entity.nextVersion();
    entityManager.persist(entityNextVersion);

    return entityNextVersion;
  }
}
