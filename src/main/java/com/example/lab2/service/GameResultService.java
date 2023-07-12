package com.example.lab2.service;

import com.example.lab2.entity.GameResult;

import java.util.List;

public interface GameResultService {

    List<GameResult> convertToGamesResultsList(List<Long> teamsIds, List<Long> teamsScores);
}