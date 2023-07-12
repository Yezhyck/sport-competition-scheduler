package com.example.lab3.dao;

import com.example.lab3.entity.Team;

import java.util.Collection;

public interface TeamRepository extends CrudRepository<Team, Long> {

    Collection<Team> findAllByName(String name);
    Collection<Team> findAllByPageAndSize(Long page, Long size);
}