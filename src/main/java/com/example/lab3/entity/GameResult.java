package com.example.lab3.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class GameResult {
    private Long id;
    private Long gameId;
    private Long teamId;
    private Long teamScore;
    private Team team;
    private Game game;
}