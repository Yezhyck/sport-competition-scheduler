package com.example.lab2.dao;

import com.example.lab2.entity.Game;

import java.util.Collection;

public interface GameRepository extends CrudRepository<Game, Long> {

    Collection<Game> findAllByTeamName(String name);
}