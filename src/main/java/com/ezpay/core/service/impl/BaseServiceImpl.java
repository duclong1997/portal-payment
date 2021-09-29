package com.ezpay.core.service.impl;

import com.ezpay.core.service.BaseService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * @author OI
 */
@NoRepositoryBean
public class BaseServiceImpl<T, ID> implements BaseService<T, ID> {
    private JpaRepository<T, ID> repository;

    public BaseServiceImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(Sort var1) {
        return repository.findAll(var1);
    }

    @Override
    public List<T> findAllById(Iterable<ID> var1) {
        return repository.findAllById(var1);
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> var1) {
        return repository.saveAll(var1);
    }

    @Override
    public void flush() {
        repository.flush();
    }

    @Override
    public <S extends T> S saveAndFlush(S var1) {
        return repository.saveAndFlush(var1);
    }

    @Override
    public void deleteInBatch(Iterable<T> var1) {
        repository.deleteInBatch(var1);
    }

    @Override
    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }

    @Override
    public T getOne(ID var1) {
        return repository.getOne(var1);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> var1, Pageable var2) {
        return repository.findAll(var1, var2);
    }

    @Override
    public <S extends T> long count(Example<S> var1) {
        return repository.count(var1);
    }

    @Override
    public <S extends T> boolean exists(Example<S> var1) {
        return repository.exists(var1);
    }

    @Override
    public Page<T> findAll(Pageable var1) {
        return repository.findAll(var1);
    }

    @Override
    public <S extends T> S save(S var1) {
        return repository.save(var1);
    }

    @Override
    public Optional<T> findById(ID var1) {
        return repository.findById(var1);
    }

    @Override
    public boolean existsById(ID var1) {
        return repository.existsById(var1);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(ID var1) {
        repository.deleteById(var1);
    }

    @Override
    public void delete(T var1) {
        repository.delete(var1);
    }

    @Override
    public void deleteAll(Iterable<? extends T> var1) {
        repository.deleteAll(var1);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
