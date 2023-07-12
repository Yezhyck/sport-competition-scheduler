package com.example.lab3.service;

import java.util.Optional;

public interface CrudService<T, K> {

    T save(T entity);
    Optional<T> findById(K id);
    Iterable<T> findAll();
    void deleteById(K id);
}