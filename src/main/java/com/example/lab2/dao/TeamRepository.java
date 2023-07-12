package com.example.lab2.dao;

import com.example.lab2.entity.Team;

import java.util.Collection;

public interface TeamRepository extends CrudRepository<Team, Long> {

    Collection<Team> findAllByName(String name);
}