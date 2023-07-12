package com.example.lab2.service;

import com.example.lab2.entity.Team;

public interface TeamService extends CrudService<Team, Long> {

    Iterable<Team> findAllByName(String name);
}