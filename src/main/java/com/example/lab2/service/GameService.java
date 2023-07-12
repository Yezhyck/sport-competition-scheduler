package com.example.lab2.service;

import com.example.lab2.entity.Game;

public interface GameService extends CrudService<Game, Long> {

    Iterable<Game> findAllByTeamName(String teamName);
}