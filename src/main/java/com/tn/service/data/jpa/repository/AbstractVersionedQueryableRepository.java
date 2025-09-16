package com.tn.service.data.jpa.repository;

import java.util.Collection;

import jakarta.persistence.EntityManager;

import com.tn.query.jpa.AbstractQueryableRepository;
import com.tn.service.data.domain.Versioned;

public class AbstractVersionedQueryableRepository<T extends Versioned<T>> extends AbstractQueryableRepository<T>
{
  public AbstractVersionedQueryableRepository(EntityManager entityManager)
  {
    super(entityManager);
  }

  public AbstractVersionedQueryableRepository(EntityManager entityManager, Collection<String> ignoredFieldNames)
  {
    super(entityManager, ignoredFieldNames);
  }

  protected <S extends T> S saveNextVersion(S entity)
  {
    return AbstractVersionedRepository.saveNextVersion(entityManager(), entity);
  }
}
