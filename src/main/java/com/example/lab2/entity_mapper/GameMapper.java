package com.example.lab2.entity_mapper;

import com.example.lab2.entity.Game;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameMapper implements RowMapper<Game> {

    @Override
    public Game mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Game.builder()
                .id(rs.getLong("ID"))
                .beginningDateTime(rs.getTimestamp("DATE_TIME_BEGIN").toLocalDateTime())
                .isOver(rs.getInt("IS_OVER"))
                .build();
    }
}