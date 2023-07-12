package com.example.lab2.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@Builder
public class GameResult {
    private Long id;
    private Long gameId;
    private Long teamId;
    @Min(value = 0, message = "Team score should be greater than 0")
    @Max(value = 31, message = "Team score should be less than 31")
    private Long teamScore;
    private Game game;
    private Team team;
}