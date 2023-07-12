package com.example.lab3.service;

import com.example.lab3.entity.Team;

import java.util.Collection;

public interface TeamService extends CrudService<Team, Long> {

    Collection<Team> findAllByName(String name);
    Collection<Team> findAllByPageAndSize(Long page, Long size);
}