package com.tn.service.data.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.tn.service.data.domain.Direction.DESCENDING;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.tn.lang.util.Page;

class CrudRepositoryAdaptorTest
{
  private static final String CUSTOM_SORT = "someOtherField";
  private static final String DEFAULT_SORT = "someField";
  private static final int PAGE_NUMBER = 0;
  private static final int PAGE_SIZE = 10;

  @Test
  void shouldFindWithKey()
  {
    int key = 1;
    Object entity = new Object();

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findById(key)).thenReturn(Optional.of(entity));

    assertEquals(entity, new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).find(key).orElse(null));
  }

  @Test
  void shouldFindAllWithDefaultSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findAll(defaultSort())).thenReturn(entities);

    assertEquals(entities, new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).findAll());
  }

  @Test
  void shouldFindAllWithCustomSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findAll(customSort())).thenReturn(entities);

    assertEquals(entities, new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).findAll(Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldFindAllWithPaginationAndDefaultSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findAll(PageRequest.of(PAGE_NUMBER, PAGE_SIZE, defaultSort()))).thenReturn(springPage(entities));

    assertEquals(page(entities), new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).findAll(PAGE_NUMBER, PAGE_SIZE));
  }

  @Test
  void shouldFindAllWithPaginationAndCustomSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findAll(PageRequest.of(PAGE_NUMBER, PAGE_SIZE, customSort()))).thenReturn(springPage(entities));

    assertEquals(page(entities), new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).findAll(PAGE_NUMBER, PAGE_SIZE, Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldFindAllWithKeys()
  {
    List<Integer> keys = List.of(1, 2, 3);
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findAllById(keys)).thenReturn(entities);

    assertEquals(entities, new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).findAll(keys));
  }

  @Test
  void shouldFindWhereWithDefaultSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());
    String query = "field=value";

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findWhere(query, defaultSort())).thenReturn(entities);

    assertEquals(entities, new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).findWhere(query));
  }

  @Test
  void shouldFindWhereWithCustomSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());
    String query = "field=value";

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findWhere(query, customSort())).thenReturn(entities);

    assertEquals(entities, new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).findWhere(query, Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldFindWhereWithPaginationAndDefaultSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());
    String query = "field=value";

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findWhere(query, PageRequest.of(PAGE_NUMBER, PAGE_SIZE, defaultSort()))).thenReturn(springPage(entities));

    assertEquals(page(entities), new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).findWhere(query, PAGE_NUMBER, PAGE_SIZE));
  }

  @Test
  void shouldFindWhereWithPaginationAndCustomSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());
    String query = "field=value";

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Integer, Object> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findWhere(query, PageRequest.of(PAGE_NUMBER, PAGE_SIZE, customSort()))).thenReturn(springPage(entities));

    assertEquals(page(entities), new CrudRepositoryAdaptor<>(repository, DEFAULT_SORT).findWhere(query, PAGE_NUMBER, PAGE_SIZE, Set.of(CUSTOM_SORT), DESCENDING));
  }

//  @Override
//  public V insert(V value) throws InsertException
//  {
//    return repository.save(value);
//  }
//
//  @Override
//  public Collection<V> insertAll(Iterable<V> values) throws InsertException
//  {
//    return asList(repository.saveAll(values));
//  }
//
//  @Override
//  public V update(V value) throws UpdateException
//  {
//    return repository.save(value);
//  }
//
//  @Override
//  public Collection<V> updateAll(Iterable<V> values) throws UpdateException
//  {
//    return asList(repository.saveAll(values));
//  }
//
//  @Override
//  public void delete(K key) throws DeleteException
//  {
//    repository.deleteById(key);
//  }
//
//  @Override
//  public void deleteAll(Iterable<K> keys) throws DeleteException
//  {
//    repository.deleteAllById(keys);
//  }

  private Sort customSort()
  {
    return Sort.by(Sort.Direction.DESC, CUSTOM_SORT);
  }

  private Sort defaultSort()
  {
    return Sort.by(Sort.Direction.ASC, DEFAULT_SORT);
  }

  private Page<Object> page(List<Object> entities)
  {
    return new Page<>(entities, PAGE_NUMBER, PAGE_SIZE, entities.size(), 1);
  }

  private org.springframework.data.domain.Page<Object> springPage(List<Object> entities)
  {
    return new PageImpl<>(entities, PageRequest.of(PAGE_NUMBER, PAGE_SIZE), entities.size());
  }
}
