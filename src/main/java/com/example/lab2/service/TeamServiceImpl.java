package com.example.lab2.service;

import com.example.lab2.dao.TeamRepositoryImpl;
import com.example.lab2.entity.Team;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepositoryImpl teamRepository;

    public TeamServiceImpl(TeamRepositoryImpl teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public Team save(Team entity) {
        return teamRepository.save(entity);
    }

    @Override
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }

    @Override
    public Collection<Team> findAll() {
        return teamRepository.findAll();
    }

    @Override
    public Collection<Team> findAllByName(String name) {
        return teamRepository.findAllByName(name);
    }
}