package com.tn.service.data.jpa.repository;

import static java.util.Comparator.comparing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.lenient;

import static com.tn.lang.Iterables.asSet;
import static com.tn.lang.Iterables.asStream;
import static com.tn.lang.util.stream.Collectors.by;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Sort;

import com.tn.query.DefaultQueryParser;
import com.tn.query.Mapper;
import com.tn.query.QueryParser;
import com.tn.query.java.Getter;
import com.tn.query.java.JavaPredicateFactory;

public class QueryablePagingAndSortingCrudRepositoryMocks
{
  private QueryablePagingAndSortingCrudRepositoryMocks() {}

  @SafeVarargs
  public static <T, ID> void initializeFindMethods(
    QueryablePagingAndSortingCrudRepository<T, ID> repository,
    Getter<T> idGetter,
    Collection<Getter<T>> getters,
    Collection<Mapper> mappers,
    T... entities
  )
  {
    Map<String, Getter<T>> gettersByName = new HashMap<>();
    gettersByName.put(idGetter.name(), idGetter);
    gettersByName.putAll(getters.stream().collect(by(Getter::name)));

    Function<Sort, Comparator<T>> comparatorFactory = sort -> toComparator(gettersByName, sort);
    QueryParser<Predicate<T>> queryParser = new DefaultQueryParser<>(new JavaPredicateFactory<>(gettersByName.values()), mappers);

    lenient().when(repository.findAll(isA(Sort.class))).thenAnswer(findAllAnswer(comparatorFactory, entities));
    lenient().when(repository.findAllById(any())).thenAnswer(findAllByIdAnswer(idGetter, entities));
    lenient().when(repository.findById(any())).thenAnswer(findByIdAnswer(idGetter, entities));
    lenient().when(repository.findWhere(any(), isA(Sort.class))).thenAnswer(findWhereAnswer(queryParser, comparatorFactory, entities));
  }

  public static <T, ID> void initializeSaveMethods(QueryablePagingAndSortingCrudRepository<T, ID> repository, UnaryOperator<T> saveCallback)
  {
    lenient().when(repository.save(any())).thenAnswer(invocation -> saveCallback.apply(invocation.getArgument(0)));
    lenient().when(repository.saveAll(any())).thenAnswer(invocation -> asStream(invocation.<Iterable<T>>getArgument(0)).map(saveCallback).toList());
  }

  private static <T> Answer<?> findAllAnswer(Function<Sort, Comparator<T>> comparatorFactory, T[] entities)
  {
    return invocation ->
    {
      Sort sort = invocation.getArgument(0);
      return sort.isEmpty()
        ? List.of(entities)
        : Stream.of(entities).sorted(comparatorFactory.apply(invocation.getArgument(0))).toList();
    };
  }

  private static <T> Answer<?> findAllByIdAnswer(Getter<T> idGetter, T[] entities)
  {
    return invocation ->
    {
      Collection<?> identities = asSet((Iterable<?>)invocation.getArgument(0));
      return Stream.of(entities)
        .filter(entity -> identities.contains(idGetter.get(entity)))
        .toList();
    };
  }

  private static <T> Answer<?> findByIdAnswer(Getter<T> idGetter, T[] entities)
  {
    return invocation -> Stream.of(entities)
      .filter(entity -> Objects.equals(idGetter.get(entity), invocation.getArgument(0)))
      .findFirst();
  }

  private static <T> Answer<?> findWhereAnswer(QueryParser<Predicate<T>> queryParser, Function<Sort, Comparator<T>> comparatorFactory, T[] entities)
  {
    return invocation -> Stream.of(entities)
      .filter(queryParser.parse(invocation.getArgument(0)))
      .sorted(comparatorFactory.apply(invocation.getArgument(1)))
      .toList();
  }

  private static <T> Comparator<T> toComparator(Map<String, Getter<T>> gettersByName, Sort sort)
  {
    Iterator<Sort.Order> orderIterator = sort.iterator();

    Comparator<T> rootComparator = toComparator(gettersByName, orderIterator.next(), null);
    Comparator<T> currentComparator = rootComparator;

    while (orderIterator.hasNext()) currentComparator = toComparator(gettersByName, orderIterator.next(), currentComparator);

    return rootComparator;
  }

  private static <T> Comparator<T> toComparator(Map<String, Getter<T>> gettersByName, Sort.Order order, Comparator<T> currentComparator)
  {
    Comparator<T> comparator = currentComparator == null
      ? comparing(toComparable(gettersByName, order))
      : currentComparator.thenComparing(toComparable(gettersByName, order));

    return order.getDirection().isAscending() ? comparator : comparator.reversed();
  }

  private static <T, U> Function<T, ? extends Comparable<U>> toComparable(Map<String, Getter<T>> gettersByName, Sort.Order order)
  {
    //noinspection unchecked
    return entity -> (Comparable<U>)gettersByName.get(order.getProperty()).get(entity);
  }
}
