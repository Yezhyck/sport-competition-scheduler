package com.example.lab2.service;

import com.example.lab2.dao.GameRepositoryImpl;

import com.example.lab2.entity.Game;
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
}