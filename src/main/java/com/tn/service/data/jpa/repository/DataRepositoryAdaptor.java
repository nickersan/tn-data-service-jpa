package com.tn.service.data.jpa.repository;

import static java.util.Collections.emptyList;

import static com.tn.lang.Iterables.asArray;
import static com.tn.lang.Iterables.asList;
import static com.tn.lang.Iterables.isEmpty;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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

public class DataRepositoryAdaptor<T, ID> implements DataRepository<T, ID>
{
  private final Function<Sort, Iterable<T>> findAll;
  private final Function<PageRequest, org.springframework.data.domain.Page<T>> findAllPaginated;
  private final Function<Iterable<ID>, Iterable<T>> findAllById;
  private final Function<ID, Optional<T>> findById;
  private final BiFunction<String, Sort, Iterable<T>> findWhere;
  private final BiFunction<String, PageRequest, org.springframework.data.domain.Page<T>> findWherePaginated;
  private final UnaryOperator<T> save;
  private final UnaryOperator<Iterable<T>> saveAll;
  private final Consumer<ID> deleteById;
  private final Consumer<Iterable<ID>> deleteAllById;
  private final String[] defaultSort;

  public static <T> DataRepositoryAdaptor<T, Long> newInstance(LongIdPagingAndSortingRepository<T, ?> repository, String... defaultSort)
  {
    return new DataRepositoryAdaptor<>(
      repository::findAll,
      repository::findAll,
      repository::findAllById,
      repository::findById,
      repository::findWhere,
      repository::findWhere,
      repository::save,
      repository::saveAll,
      repository::deleteById,
      repository::deleteAllById,
      defaultSort
    );
  }

  DataRepositoryAdaptor(
    Function<Sort, Iterable<T>> findAll,
    Function<PageRequest, org.springframework.data.domain.Page<T>> findAllPaginated,
    Function<Iterable<ID>, Iterable<T>> findAllById,
    Function<ID, Optional<T>> findById,
    BiFunction<String, Sort, Iterable<T>> findWhere,
    BiFunction<String, PageRequest, org.springframework.data.domain.Page<T>> findWherePaginated,
    UnaryOperator<T> save,
    UnaryOperator<Iterable<T>> saveAll,
    Consumer<ID> deleteById,
    Consumer<Iterable<ID>> deleteAllById,
    String... defaultSort
  )
  {
    this.findAll = findAll;
    this.findAllPaginated = findAllPaginated;
    this.findAllById = findAllById;
    this.findById = findById;
    this.findWhere = findWhere;
    this.findWherePaginated = findWherePaginated;
    this.save = save;
    this.saveAll = saveAll;
    this.deleteById = deleteById;
    this.deleteAllById = deleteAllById;
    this.defaultSort = defaultSort;
  }

  @Override
  public Optional<T> find(ID identity) throws FindException
  {
    return findById.apply(identity);
  }

  @Override
  public Collection<T> findAll(Iterable<String> sort, Direction direction) throws FindException
  {
    return asList(findAll.apply(asSort(sort, direction)));
  }

  @Override
  public Collection<T> findAll(Iterable<ID> identities) throws FindException
  {
    return asList(findAllById.apply(identities));
  }

  @Override
  public Page<T> findAll(int pageNumber, int pageSize, Iterable<String> sort, Direction direction) throws FindException
  {
    return asPage(findAllPaginated.apply(PageRequest.of(pageNumber, pageSize, asSort(sort, direction))));
  }

  @Override
  public Collection<T> findWhere(String query, Iterable<String> sort, Direction direction) throws FindException
  {
    return asList(findWhere.apply(query, asSort(sort, direction)));
  }

  @Override
  public Page<T> findWhere(String query, int pageNumber, int pageSize, Iterable<String> sort, Direction direction) throws FindException
  {
    return asPage(findWherePaginated.apply(query, PageRequest.of(pageNumber, pageSize, asSort(sort, direction))));
  }

  @Override
  public T insert(T value) throws InsertException
  {
    return save.apply(value);
  }

  @Override
  public Collection<T> insertAll(Iterable<T> values) throws InsertException
  {
    return asList(saveAll.apply(values));
  }

  @Override
  public T update(T value) throws UpdateException
  {
    return save.apply(value);
  }

  @Override
  public Collection<T> updateAll(Iterable<T> values) throws UpdateException
  {
    return asList(saveAll.apply(values));
  }

  @Override
  @Transactional
  public Optional<T> delete(ID identity) throws DeleteException
  {
    Optional<T> entity = findById.apply(identity);
    if (entity.isPresent()) deleteById.accept(identity);
    
    return entity;
  }

  @Override
  @Transactional
  public Collection<T> deleteAll(Iterable<ID> identities) throws DeleteException
  {
    Collection<T> entities = asList(findAllById.apply(identities));
    if (entities.size() != Iterables.size(identities)) return emptyList();

    deleteAllById.accept(identities);

    return entities;
  }

  private Page<T> asPage(org.springframework.data.domain.Page<T> page)
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
