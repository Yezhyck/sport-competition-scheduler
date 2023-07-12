package com.example.lab2.dao;

import java.util.Collection;
import java.util.Optional;

public interface CrudRepository<T, K> {

    T save(T entity);

    Optional<T> findById(K id);

    Collection<T> findAll();

    void deleteById(K id);
}