package com.ezpay.core.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * @author OI
 */
public interface BaseService<T, ID> {
    List<T> findAll();

    List<T> findAll(Sort var1);

    List<T> findAllById(Iterable<ID> var1);

    <S extends T> List<S> saveAll(Iterable<S> var1);

    void flush();

    <S extends T> S saveAndFlush(S var1);

    void deleteInBatch(Iterable<T> var1);

    void deleteAllInBatch();

    T getOne(ID var1);

    <S extends T> Page<S> findAll(Example<S> var1, Pageable var2);

    <S extends T> long count(Example<S> var1);

    <S extends T> boolean exists(Example<S> var1);

    Page<T> findAll(Pageable var1);

    <S extends T> S save(S var1);

    Optional<T> findById(ID var1);

    boolean existsById(ID var1);

    long count();

    void deleteById(ID var1);

    void delete(T var1);

    void deleteAll(Iterable<? extends T> var1);

    void deleteAll();
}
