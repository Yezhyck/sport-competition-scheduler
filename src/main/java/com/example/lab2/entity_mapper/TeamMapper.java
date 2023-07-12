package com.example.lab2.entity_mapper;

import com.example.lab2.entity.Team;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamMapper implements RowMapper<Team> {

    @Override
    public Team mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Team.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .build();
    }
}