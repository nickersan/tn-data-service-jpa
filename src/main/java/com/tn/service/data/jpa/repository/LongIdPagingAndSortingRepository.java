package com.tn.service.data.jpa.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.tn.query.jpa.QueryableRepository;

public interface LongIdPagingAndSortingRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryableRepository<T>
{
  Iterable<T> findAll();

  Optional<T> findById(long id);

  Iterable<T> findAllById(Iterable<Long> id);

  T save(T entity);

  @Transactional
  Iterable<T> saveAll(Iterable<T> entities);

  @Transactional
  void deleteById(long id);

  @Transactional
  default void deleteAllById(Iterable<Long> id)
  {
    id.forEach(this::deleteById);
  }
}
