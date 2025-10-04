package com.tn.service.data.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.tn.service.data.domain.Direction.DESCENDING;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.tn.lang.util.Page;

@ExtendWith(MockitoExtension.class)
class DataRepositoryAdaptorTest
{
  private static final String CUSTOM_SORT = "someOtherField";
  private static final String DEFAULT_SORT = "someField";
  private static final int PAGE_NUMBER = 0;
  private static final int PAGE_SIZE = 10;

  @Mock
  Function<Sort, Iterable<Object>> findAll;
  @Mock
  Function<PageRequest, org.springframework.data.domain.Page<Object>> findAllPaginated;
  @Mock
  Function<Iterable<Integer>, Iterable<Object>> findAllById;
  @Mock
  Function<Integer, Optional<Object>> findById;
  @Mock
  BiFunction<String, Sort, Iterable<Object>> findWhere;
  @Mock
  BiFunction<String, PageRequest, org.springframework.data.domain.Page<Object>> findWherePaginated;
  @Mock
  UnaryOperator<Object> save;
  @Mock
  UnaryOperator<Iterable<Object>> saveAll;
  @Mock
  Consumer<Integer> deleteById;
  @Mock
  Consumer<Iterable<Integer>> deleteAllById;

  private DataRepositoryAdaptor<Object, Integer> dataRepositoryAdaptor;

  @BeforeEach
  void initializeDataRepositoryAdaptor()
  {
    dataRepositoryAdaptor = new DataRepositoryAdaptor<>(
      findAll,
      findAllPaginated,
      findAllById,
      findById,
      findWhere,
      findWherePaginated,
      save,
      saveAll,
      deleteById,
      deleteAllById,
      DEFAULT_SORT
    );
  }

  @Test
  void shouldFindWithIdentity()
  {
    int identity = 1;
    Object entity = new Object();

    when(findById.apply(identity)).thenReturn(Optional.of(entity));

    assertEquals(entity, dataRepositoryAdaptor.find(identity).orElse(null));
  }

  @Test
  void shouldFindAllWithSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    when(findAll.apply(sort())).thenReturn(entities);

    assertEquals(entities, dataRepositoryAdaptor.findAll(Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldFindAllWithPaginationAndSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    when(findAllPaginated.apply(PageRequest.of(PAGE_NUMBER, PAGE_SIZE, sort()))).thenReturn(springPage(entities));

    assertEquals(page(entities), dataRepositoryAdaptor.findAll(PAGE_NUMBER, PAGE_SIZE, Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldFindAllWithIdentities()
  {
    List<Integer> identities = List.of(1, 2, 3);
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    when(findAllById.apply(identities)).thenReturn(entities);

    assertEquals(entities, dataRepositoryAdaptor.findAll(identities));
  }

  @Test
  void shouldFindWhereWithSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());
    String query = "field=value";

    when(findWhere.apply(query, sort())).thenReturn(entities);

    assertEquals(entities, dataRepositoryAdaptor.findWhere(query, Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldFindWhereWithPaginationAndSort()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());
    String query = "field=value";

    when(findWherePaginated.apply(query, PageRequest.of(PAGE_NUMBER, PAGE_SIZE, sort()))).thenReturn(springPage(entities));

    assertEquals(page(entities), dataRepositoryAdaptor.findWhere(query, PAGE_NUMBER, PAGE_SIZE, Set.of(CUSTOM_SORT), DESCENDING));
  }

  @Test
  void shouldInsert()
  {
    Object entity = new Object();

    when(save.apply(entity)).thenReturn(entity);

    assertEquals(entity, dataRepositoryAdaptor.insert(entity));
  }

  @Test
  void shouldInsertAll()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    when(saveAll.apply(entities)).thenReturn(entities);

    assertEquals(entities, dataRepositoryAdaptor.insertAll(entities));
  }

  @Test
  void shouldUpdate()
  {
    Object entity = new Object();

    when(save.apply(entity)).thenReturn(entity);

    assertEquals(entity, dataRepositoryAdaptor.update(entity));
  }

  @Test
  void shouldUpdateAll()
  {
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    when(saveAll.apply(entities)).thenReturn(entities);

    assertEquals(entities, dataRepositoryAdaptor.updateAll(entities));
  }

  @Test
  void shouldDelete()
  {
    int identity = 1;
    Object entity = new Object();

    when(findById.apply(identity)).thenReturn(Optional.of(entity));

    assertEquals(entity, dataRepositoryAdaptor.delete(identity).orElse(null));

    verify(deleteById).accept(identity);
  }

  @Test
  void shouldDeleteAll()
  {
    List<Integer> identities = List.of(1, 2, 3);
    List<Object> entities = List.of(new Object(), new Object(), new Object());

    when(findAllById.apply(identities)).thenReturn(entities);

    assertEquals(entities, dataRepositoryAdaptor.deleteAll(identities));

    verify(deleteAllById).accept(identities);
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
