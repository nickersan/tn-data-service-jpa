package com.tn.service.data.jpa.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.tn.query.jpa.QueryableRepository;

public interface QueryablePagingAndSortingCrudRepository<T, ID> extends QueryableRepository<T>, CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {}
