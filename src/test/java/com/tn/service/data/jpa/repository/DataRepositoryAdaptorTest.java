package com.tn.service.data.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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

class DataRepositoryAdaptorTest
{
  private static final String CUSTOM_SORT = "someOtherField";
  private static final String DEFAULT_SORT = "someField";
  private static final int PAGE_NUMBER = 0;
  private static final int PAGE_SIZE = 10;

  @Test
  void shouldFindWithIdentity()
  {
    int identity = 1;
    Object entity = new Object();

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findById(identity)).thenReturn(Optional.of(entity));

    assertEquals(entity, new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).find(identity).orElse(null));
  }

  @Test
  void shouldFindAllWithSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findAll(sort())).thenReturn(entities);

    assertEquals(entities, new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).findAll(Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldFindAllWithPaginationAndSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findAll(PageRequest.of(PAGE_NUMBER, PAGE_SIZE, sort()))).thenReturn(springPage(entities));

    assertEquals(page(entities), new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).findAll(PAGE_NUMBER, PAGE_SIZE, Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldFindAllWithIdentities()
  {
    List<Integer> identities = List.of(1, 2, 3);
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findAllById(identities)).thenReturn(entities);

    assertEquals(entities, new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).findAll(identities));
  }

  @Test
  void shouldFindWhereWithSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());
    String query = "field=value";

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findWhere(query, sort())).thenReturn(entities);

    assertEquals(entities, new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).findWhere(query, Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldFindWhereWithPaginationAndSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());
    String query = "field=value";

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findWhere(query, PageRequest.of(PAGE_NUMBER, PAGE_SIZE, sort()))).thenReturn(springPage(entities));

    assertEquals(page(entities), new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).findWhere(query, PAGE_NUMBER, PAGE_SIZE, Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldInsert()
  {
    Object entity = new Object();

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.save(entity)).thenReturn(entity);

    assertEquals(entity, new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).insert(entity));
  }

  @Test
  void shouldInsertAll()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.saveAll(entities)).thenReturn(entities);

    assertEquals(entities, new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).insertAll(entities));
  }

  @Test
  void shouldUpdate()
  {
    Object entity = new Object();

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.save(entity)).thenReturn(entity);

    assertEquals(entity, new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).update(entity));
  }

  @Test
  void shouldUpdateAll()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.saveAll(entities)).thenReturn(entities);

    assertEquals(entities, new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).updateAll(entities));
  }

  @Test
  void shouldDelete()
  {
    int identity = 1;
    Object entity = new Object();

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findById(identity)).thenReturn(Optional.of(entity));

    new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).delete(identity);

    verify(repository).delete(entity);
  }

  @Test
  void shouldDeleteAll()
  {
    List<Integer> identities = List.of(1, 2, 3);
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    @SuppressWarnings("unchecked")
    QueryablePagingAndSortingCrudRepository<Object, Integer> repository = mock(QueryablePagingAndSortingCrudRepository.class);
    when(repository.findAllById(identities)).thenReturn(entities);

    new DataRepositoryAdaptor<>(repository, DEFAULT_SORT).deleteAll(identities);

    verify(repository).deleteAll(entities);
  }

  private Sort sort()
  {
    return Sort.by(Sort.Direction.DESC, CUSTOM_SORT);
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
