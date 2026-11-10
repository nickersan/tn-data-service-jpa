package com.tn.service.data.jpa.repository;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;

class LongIdPagingAndSortingRepositoryTest
{
  @Test
  void shouldDeleteAllByIdOneAtATime()
  {
    @SuppressWarnings("unchecked")
    LongIdPagingAndSortingRepository<Object, Object> repository = mock(LongIdPagingAndSortingRepository.class);
    List<Long> identities = List.of(1L, 2L, 3L);

    doCallRealMethod().when(repository).deleteAllById(identities);

    repository.deleteAllById(identities);

    verify(repository).deleteById(1L);
    verify(repository).deleteById(2L);
    verify(repository).deleteById(3L);
  }

  @Test
  void shouldDeleteNothingWhenNoIdentities()
  {
    @SuppressWarnings("unchecked")
    LongIdPagingAndSortingRepository<Object, Object> repository = mock(LongIdPagingAndSortingRepository.class);
    List<Long> identities = List.of();

    doCallRealMethod().when(repository).deleteAllById(identities);

    repository.deleteAllById(identities);

    verify(repository, never()).deleteById(anyLong());
  }
}
