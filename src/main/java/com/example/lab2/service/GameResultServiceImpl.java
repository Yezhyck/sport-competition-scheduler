package com.example.lab2.service;

import com.example.lab2.entity.GameResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameResultServiceImpl implements GameResultService {

    @Override
    public List<GameResult> convertToGamesResultsList(List<Long> teamsIds, List<Long> teamsScores) {
        List<GameResult> gameResultList = new ArrayList<>();
        for (int i = 0; i < teamsIds.size(); i++) {
            gameResultList.add(GameResult.builder()
                    .teamId(teamsIds.get(i))
                    .teamScore(teamsScores.get(i))
                    .build());
        }
        return gameResultList;
    }
}