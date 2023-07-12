package com.example.lab2.config;

import com.example.lab2.dao.*;
import com.example.lab2.entity.Game;
import com.example.lab2.entity.Team;
import com.example.lab2.entity_mapper.GameMapper;
import com.example.lab2.entity_mapper.TeamMapper;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class SpringConfig {

    @Bean
    @Scope("singleton")
    public DataSource dataSource() throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();

        dataSource.setURL("jdbc:oracle:thin:@localhost:1521:XE");
        dataSource.setUser("your_oracle_user");
        dataSource.setPassword("your_oracle_user_password");

        return dataSource;
    }

    @Bean
    @Scope("singleton")
    public RowMapper<Team> teamRowMapper() {
        return new TeamMapper();
    }

    @Bean
    @Scope("singleton")
    public RowMapper<Game> gameRowMapper() {
        return new GameMapper();
    }

    @Bean
    @Scope("prototype")
    public TeamRepository teamRepository() throws SQLException {
        return new TeamRepositoryImpl(dataSource(), teamRowMapper());
    }

    @Bean
    @Scope("prototype")
    public GameRepository gameRepository() throws SQLException {
        return new GameRepositoryImpl(dataSource(), gameRowMapper());
    }
}