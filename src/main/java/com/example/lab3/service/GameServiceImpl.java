package com.example.lab3.service;

import com.example.lab3.dao.GameRepositoryImpl;

import com.example.lab3.entity.Game;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepositoryImpl gameRepository;

    public GameServiceImpl(GameRepositoryImpl gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game save(Game entity) {
        return gameRepository.save(entity);
    }

    @Override
    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        gameRepository.deleteById(id);
    }

    @Override
    public Collection<Game> findAll() {
        return gameRepository.findAll();
    }

    @Override
    public Collection<Game> findAllByTeamName(String name) {
        return gameRepository.findAllByTeamName(name);
    }

    @Override
    public Collection<Game> findAllByPageAndSize(Long page, Long size) {
        return gameRepository.findAllByPageAndSize(page, size);
    }
}