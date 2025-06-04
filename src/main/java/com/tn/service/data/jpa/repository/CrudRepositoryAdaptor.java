package com.tn.service.data.jpa.repository;

import static com.tn.lang.Iterables.asArray;
import static com.tn.lang.Iterables.asList;
import static com.tn.lang.Iterables.isEmpty;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.tn.lang.util.Page;
import com.tn.service.data.domain.Direction;
import com.tn.service.data.repository.DataRepository;
import com.tn.service.data.repository.DeleteException;
import com.tn.service.data.repository.FindException;
import com.tn.service.data.repository.InsertException;
import com.tn.service.data.repository.UpdateException;

public class CrudRepositoryAdaptor<K, V> implements DataRepository<K, V>
{
  private final QueryablePagingAndSortingCrudRepository<K, V> repository;
  private final String[] defaultSort;

  public CrudRepositoryAdaptor(QueryablePagingAndSortingCrudRepository<K, V> repository, String... defaultSort)
  {
    this.repository = repository;
    this.defaultSort = defaultSort;
  }

  @Override
  public Optional<V> find(K key) throws FindException
  {
    return repository.findById(key);
  }

  @Override
  public Collection<V> findAll(Iterable<String> sort, Direction direction) throws FindException
  {
    return asList(repository.findAll(asSort(sort, direction)));
  }

  @Override
  public Page<V> findAll(int pageNumber, int pageSize, Iterable<String> sort, Direction direction) throws FindException
  {
    return asPage(repository.findAll(PageRequest.of(pageNumber, pageSize, asSort(sort, direction))));
  }

  @Override
  public Collection<V> findAll(Iterable<K> keys) throws FindException
  {
    return asList(repository.findAllById(keys));
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
  public void delete(K key) throws DeleteException
  {
    repository.deleteById(key);
  }

  @Override
  public void deleteAll(Iterable<K> keys) throws DeleteException
  {
    repository.deleteAllById(keys);
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
