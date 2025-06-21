package com.tn.service.data.jpa.repository;

import static java.util.Collections.emptyList;

import static com.tn.lang.Iterables.asArray;
import static com.tn.lang.Iterables.asList;
import static com.tn.lang.Iterables.isEmpty;

import java.util.Collection;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.tn.lang.Iterables;
import com.tn.lang.util.Page;
import com.tn.service.data.domain.Direction;
import com.tn.service.data.repository.DataRepository;
import com.tn.service.data.repository.DeleteException;
import com.tn.service.data.repository.FindException;
import com.tn.service.data.repository.InsertException;
import com.tn.service.data.repository.UpdateException;

public class DataRepositoryAdaptor<V, ID> implements DataRepository<V, ID>
{
  private final QueryablePagingAndSortingCrudRepository<V, ID> repository;
  private final String[] defaultSort;

  public DataRepositoryAdaptor(QueryablePagingAndSortingCrudRepository<V, ID> repository, String... defaultSort)
  {
    this.repository = repository;
    this.defaultSort = defaultSort;
  }

  @Override
  public Optional<V> find(ID identity) throws FindException
  {
    return repository.findById(identity);
  }

  @Override
  public Collection<V> findAll(Iterable<String> sort, Direction direction) throws FindException
  {
    return asList(repository.findAll(asSort(sort, direction)));
  }

  @Override
  public Collection<V> findAll(Iterable<ID> identities) throws FindException
  {
    return asList(repository.findAllById(identities));
  }

  @Override
  public Page<V> findAll(int pageNumber, int pageSize, Iterable<String> sort, Direction direction) throws FindException
  {
    return asPage(repository.findAll(PageRequest.of(pageNumber, pageSize, asSort(sort, direction))));
  }

  @Override
  public Collection<V> findWhere(String query, Iterable<String> sort, Direction direction) throws FindException
  {
    return asList(repository.findWhere(query, asSort(sort, direction)));
  }

  @Override
  public Page<V> findWhere(String query, int pageNumber, int pageSize, Iterable<String> sort, Direction direction) throws FindException
  {
    return asPage(repository.findWhere(query, PageRequest.of(pageNumber, pageSize, asSort(sort, direction))));
  }

  @Override
  public V insert(V value) throws InsertException
  {
    return repository.save(value);
  }

  @Override
  public Collection<V> insertAll(Iterable<V> values) throws InsertException
  {
    return asList(repository.saveAll(values));
  }

  @Override
  public V update(V value) throws UpdateException
  {
    return repository.save(value);
  }

  @Override
  public Collection<V> updateAll(Iterable<V> values) throws UpdateException
  {
    return asList(repository.saveAll(values));
  }

  @Override
  @Transactional
  public Optional<V> delete(ID identity) throws DeleteException
  {
    Optional<V> entity = repository.findById(identity);
    entity.ifPresent(repository::delete);
    
    return entity;
  }

  @Override
  @Transactional
  public Collection<V> deleteAll(Iterable<ID> identities) throws DeleteException
  {
    Collection<V> entities = asList(repository.findAllById(identities));
    if (entities.size() != Iterables.size(identities)) return emptyList();

    repository.deleteAll(entities);

    return entities;
  }

  private Page<V> asPage(org.springframework.data.domain.Page<V> page)
  {
    return new Page<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
  }

  private Sort asSort(Iterable<String> sort, Direction direction)
  {
    return Sort.by(
      direction.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC,
      isEmpty(sort) ? defaultSort : asArray(sort, String[]::new)
    );
  }
}
