package com.example.lab3.service;

import com.example.lab3.entity.Game;

import java.util.Collection;

public interface GameService extends CrudService<Game, Long> {

    Collection<Game> findAllByTeamName(String teamName);
    Collection<Game> findAllByPageAndSize(Long page, Long size);
}