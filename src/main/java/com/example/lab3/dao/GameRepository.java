package com.example.lab3.dao;

import com.example.lab3.entity.Game;

import java.util.Collection;

public interface GameRepository extends CrudRepository<Game, Long> {

    Collection<Game> findAllByTeamName(String name);
    Collection<Game> findAllByPageAndSize(Long page, Long size);
}