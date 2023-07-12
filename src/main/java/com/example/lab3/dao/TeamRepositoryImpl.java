package com.example.lab3.dao;

import com.example.lab3.entity.Game;
import com.example.lab3.entity.GameResult;
import com.example.lab3.entity.Team;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TeamRepositoryImpl implements TeamRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Team> teamRowMapper;

    public TeamRepositoryImpl(DataSource dataSource, RowMapper<Team> teamRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.teamRowMapper = teamRowMapper;
    }

    @Override
    public Team save(Team entity) {
        if (findById(entity.getId()).isPresent()) {
            jdbcTemplate.update("UPDATE TEAMS T SET T.NAME = ? WHERE T.ID = ?", entity.getName(), entity.getId());
        } else {
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(con -> {
                PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO TEAMS (NAME) VALUES (?)",
                        new String[]{"ID"});
                preparedStatement.setString(1, entity.getName());

                return preparedStatement;
            }, generatedKeyHolder);

            entity.setId(Objects.requireNonNull(generatedKeyHolder.getKey()).longValue());
        }

        return findById(entity.getId()).orElse(entity);
    }

    @Override
    public Optional<Team> findById(Long id) {
        Optional<Team> team = jdbcTemplate.query("SELECT * FROM TEAMS T WHERE T.ID = ?", teamRowMapper, id).stream()
                .findAny();

        if (team.isPresent()) {
            Map<Long, GameResult> teamGameResultMap = new LinkedHashMap<>();
            Map<Long, Game> gameMap = new LinkedHashMap<>();
            Map<Long, GameResult> gameResultMap = new LinkedHashMap<>();

            jdbcTemplate.query("SELECT GR.ID AS GR_ID, GR.ID_GAME AS GR_ID_GAME, GR.ID_TEAM AS GR_ID_TEAM, " +
                            "GR.SCORE AS GR_SCORE, G.ID AS G_ID, G.DATE_TIME_BEGIN AS G_DATE_TIME_BEGIN, " +
                            "G.IS_OVER AS G_IS_OVER, GR1.ID AS GR1_ID, GR1.ID_GAME AS GR1_ID_GAME, " +
                            "GR1.ID_TEAM AS GR1_ID_TEAM, GR1.SCORE AS GR1_SCORE, T2.ID AS T2_ID, T2.NAME AS T2_NAME " +
                            "FROM TEAMS T INNER JOIN GAMES_RESULTS GR ON T.ID = ? AND T.ID = GR.ID_TEAM INNER JOIN " +
                            "GAMES G ON G.ID = GR.ID_GAME INNER JOIN GAMES_RESULTS GR1 ON G.ID = GR1.ID_GAME INNER " +
                            "JOIN TEAMS T2 ON T2.ID <> T.ID AND T2.ID = GR1.ID_TEAM",
                    (rs, rowNum) -> {
                        Team team2 = Team.builder()
                                .id(rs.getLong("T2_ID"))
                                .name(rs.getString("T2_NAME"))
                                .build();
                        Long gameId = rs.getLong("G_ID");
                        Game mapGame = gameMap.get(gameId);

                        if (!gameMap.containsKey(gameId)) {
                            mapGame = Game.builder()
                                    .id(gameId)
                                    .beginningDateTime(rs.getTimestamp("G_DATE_TIME_BEGIN").toLocalDateTime())
                                    .isOver(rs.getInt("G_IS_OVER"))
                                    .build();

                            gameMap.put(gameId, mapGame);
                        }

                        Long gameResult1Id = rs.getLong("GR1_ID");

                        if (!gameResultMap.containsKey(gameResult1Id)) {
                            gameResultMap.put(gameResult1Id, GameResult.builder()
                                    .id(gameResult1Id)
                                    .gameId(rs.getLong("GR1_ID_GAME"))
                                    .teamId(rs.getLong("GR1_ID_TEAM"))
                                    .teamScore(rs.getLong("GR1_SCORE"))
                                    .team(team2)
                                    .build());
                        }

                        Long gameResultId = rs.getLong("GR_ID");

                        if (!teamGameResultMap.containsKey(gameResultId)) {
                            teamGameResultMap.put(gameResultId, GameResult.builder()
                                    .id(gameResultId)
                                    .gameId(rs.getLong("GR_ID_GAME"))
                                    .teamId(rs.getLong("GR_ID_TEAM"))
                                    .teamScore(rs.getLong("GR_SCORE"))
                                    .game(mapGame)
                                    .build());
                        }

                        return null;
                    }, id);

            gameResultMap.forEach((key, value) -> gameMap.get(value.getGameId())
                    .getGameResultList()
                    .add(value));

            team.get().setGameResultList(List.copyOf(teamGameResultMap.values()));
        }
        return team;
    }

    @Override
    public Collection<Team> findAll() {
        Map<Long, Team> teamMap = new LinkedHashMap<>();
        Map<Long, GameResult> teamGameResultMap = new LinkedHashMap<>();
        Map<Long, Game> gameMap = new LinkedHashMap<>();
        Map<Long, GameResult> gameResultMap = new LinkedHashMap<>();

        jdbcTemplate.query("SELECT T.ID AS T_ID, T.NAME AS T_NAME, GR.ID AS GR_ID, GR.ID_GAME AS GR_ID_GAME, " +
                        "GR.ID_TEAM AS GR_ID_TEAM, GR.SCORE AS GR_SCORE, G.ID AS G_ID, G.DATE_TIME_BEGIN AS " +
                        "G_DATE_TIME_BEGIN,  G.IS_OVER AS G_IS_OVER, GR1.ID AS GR1_ID, GR1.ID_GAME AS GR1_ID_GAME, " +
                        "GR1.ID_TEAM AS GR1_ID_TEAM, GR1.SCORE AS GR1_SCORE, T2.ID AS T2_ID, T2.NAME AS T2_NAME " +
                        "FROM TEAMS T INNER JOIN GAMES_RESULTS GR ON T.ID = GR.ID_TEAM INNER JOIN " +
                        "GAMES G ON G.ID = GR.ID_GAME INNER JOIN GAMES_RESULTS GR1 ON G.ID = GR1.ID_GAME INNER " +
                        "JOIN TEAMS T2 ON T2.ID <> T.ID AND T2.ID = GR1.ID_TEAM",
                (rs, rowNum) -> {
                    Long gameId = rs.getLong("G_ID");
                    Game mapGame = gameMap.get(gameId);

                    if (!gameMap.containsKey(gameId)) {
                        mapGame = Game.builder()
                                .id(gameId)
                                .beginningDateTime(rs.getTimestamp("G_DATE_TIME_BEGIN").toLocalDateTime())
                                .isOver(rs.getInt("G_IS_OVER"))
                                .build();

                        gameMap.put(gameId, mapGame);
                    }

                    Long gameResult1Id = rs.getLong("GR1_ID");

                    if (!gameResultMap.containsKey(gameResult1Id)) {
                        gameResultMap.put(gameResult1Id, GameResult.builder()
                                .id(gameResult1Id)
                                .gameId(rs.getLong("GR1_ID_GAME"))
                                .teamId(rs.getLong("GR1_ID_TEAM"))
                                .teamScore(rs.getLong("GR1_SCORE"))
                                .team(Team.builder()
                                        .id(rs.getLong("T2_ID"))
                                        .name(rs.getString("T2_NAME"))
                                        .build())
                                .build());
                    }

                    Long gameResultId = rs.getLong("GR_ID");

                    if (!teamGameResultMap.containsKey(gameResultId)) {
                        teamGameResultMap.put(gameResultId, GameResult.builder()
                                .id(gameResultId)
                                .gameId(rs.getLong("GR_ID_GAME"))
                                .teamId(rs.getLong("GR_ID_TEAM"))
                                .teamScore(rs.getLong("GR_SCORE"))
                                .game(mapGame)
                                .build());
                    }

                    Long teamId = rs.getLong("T_ID");

                    if (!teamMap.containsKey(teamId)) {
                        teamMap.put(teamId, Team.builder()
                                .id(teamId)
                                .name(rs.getString("T_NAME"))
                                .build());
                    }

                    return null;
                });

        gameResultMap.forEach((key, value) -> gameMap.get(value.getGameId())
                .getGameResultList()
                .add(value));

        teamGameResultMap.forEach((key, value) -> teamMap.get(value.getTeamId())
                .getGameResultList()
                .add(value));

        return teamMap.values();
    }

    @Override
    public Collection<Team> findAllByName(String name) {
        Map<Long, Team> teamMap = new LinkedHashMap<>();
        Map<Long, GameResult> teamGameResultMap = new LinkedHashMap<>();
        Map<Long, Game> gameMap = new LinkedHashMap<>();
        Map<Long, GameResult> gameResultMap = new LinkedHashMap<>();

        jdbcTemplate.query("SELECT T.ID AS T_ID, T.NAME AS T_NAME, GR.ID AS GR_ID, GR.ID_GAME AS GR_ID_GAME, " +
                        "GR.ID_TEAM AS GR_ID_TEAM, GR.SCORE AS GR_SCORE, G.ID AS G_ID, G.DATE_TIME_BEGIN AS " +
                        "G_DATE_TIME_BEGIN,  G.IS_OVER AS G_IS_OVER, GR1.ID AS GR1_ID, GR1.ID_GAME AS GR1_ID_GAME, " +
                        "GR1.ID_TEAM AS GR1_ID_TEAM, GR1.SCORE AS GR1_SCORE, T2.ID AS T2_ID, T2.NAME AS T2_NAME " +
                        "FROM TEAMS T INNER JOIN GAMES_RESULTS GR ON T.NAME LIKE '%" + name + "%' AND " +
                        "T.ID = GR.ID_TEAM INNER JOIN GAMES G ON G.ID = GR.ID_GAME INNER JOIN GAMES_RESULTS GR1 ON " +
                        "G.ID = GR1.ID_GAME INNER  JOIN TEAMS T2 ON T2.ID <> T.ID AND T2.ID = GR1.ID_TEAM",
                (rs, rowNum) -> {
                    Long gameId = rs.getLong("G_ID");
                    Game mapGame = gameMap.get(gameId);

                    if (!gameMap.containsKey(gameId)) {
                        mapGame = Game.builder()
                                .id(gameId)
                                .beginningDateTime(rs.getTimestamp("G_DATE_TIME_BEGIN").toLocalDateTime())
                                .isOver(rs.getInt("G_IS_OVER"))
                                .build();

                        gameMap.put(gameId, mapGame);
                    }

                    Long gameResult1Id = rs.getLong("GR1_ID");

                    if (!gameResultMap.containsKey(gameResult1Id)) {
                        gameResultMap.put(gameResult1Id, GameResult.builder()
                                .id(gameResult1Id)
                                .gameId(rs.getLong("GR1_ID_GAME"))
                                .teamId(rs.getLong("GR1_ID_TEAM"))
                                .teamScore(rs.getLong("GR1_SCORE"))
                                .team(Team.builder()
                                        .id(rs.getLong("T2_ID"))
                                        .name(rs.getString("T2_NAME"))
                                        .build())
                                .build());
                    }

                    Long gameResultId = rs.getLong("GR_ID");

                    if (!teamGameResultMap.containsKey(gameResultId)) {
                        teamGameResultMap.put(gameResultId, GameResult.builder()
                                .id(gameResultId)
                                .gameId(rs.getLong("GR_ID_GAME"))
                                .teamId(rs.getLong("GR_ID_TEAM"))
                                .teamScore(rs.getLong("GR_SCORE"))
                                .game(mapGame)
                                .build());
                    }

                    Long teamId = rs.getLong("T_ID");

                    if (!teamMap.containsKey(teamId)) {
                        teamMap.put(teamId, Team.builder()
                                .id(teamId)
                                .name(rs.getString("T_NAME"))
                                .build());
                    }

                    return null;
                });

        gameResultMap.forEach((key, value) -> gameMap.get(value.getGameId())
                .getGameResultList()
                .add(value));

        teamGameResultMap.forEach((key, value) -> teamMap.get(value.getTeamId())
                .getGameResultList()
                .add(value));

        return teamMap.values();
    }

    @Override
    public void deleteById(Long id) {
        Optional<Team> team = findById(id);

        if (team.isPresent()) {
            List<String> gameIdList = team.get().getGameResultList().stream()
                    .filter(gameResult -> gameResult.getGame().getGameResultList().size() == Game.MIN_TEAMS_AMOUNT)
                    .map(gameResult -> String.valueOf(gameResult.getGameId()))
                    .collect(Collectors.toList());

            if (!gameIdList.isEmpty()) {
                jdbcTemplate.update("DELETE FROM GAMES G WHERE G.ID IN (" +
                        String.join(", ", gameIdList) + ")");
            }

            jdbcTemplate.update("DELETE FROM TEAMS T WHERE T.ID = ?", id);
        }
    }

    @Override
    public Collection<Team> findAllByPageAndSize(Long page, Long size) {
        if (page <= 0 || size <= 0) {
            return new ArrayList<>();
        }

        long start = (page - 1) * size + 1;
        long end = start + size - 1;

        List<String> teamsIds = jdbcTemplate.query("SELECT * FROM (SELECT T.ID AS T_ID, row_number() over " +
                                "(ORDER BY T.ID) line_number FROM TEAMS T) WHERE line_number BETWEEN " + start + " AND " + end,
                        (rs, rowNum) -> rs.getLong("T_ID")).stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        if (teamsIds.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Team> teamMap = new LinkedHashMap<>();
        Map<Long, GameResult> teamGameResultMap = new LinkedHashMap<>();
        Map<Long, Game> gameMap = new LinkedHashMap<>();
        Map<Long, GameResult> gameResultMap = new LinkedHashMap<>();

        jdbcTemplate.query("SELECT T.ID AS T_ID, T.NAME AS T_NAME, GR.ID AS GR_ID, GR.ID_GAME AS GR_ID_GAME, " +
                        "GR.ID_TEAM AS GR_ID_TEAM, GR.SCORE AS GR_SCORE, G.ID AS G_ID, G.DATE_TIME_BEGIN AS " +
                        "G_DATE_TIME_BEGIN,  G.IS_OVER AS G_IS_OVER, GR1.ID AS GR1_ID, GR1.ID_GAME AS GR1_ID_GAME, " +
                        "GR1.ID_TEAM AS GR1_ID_TEAM, GR1.SCORE AS GR1_SCORE, T2.ID AS T2_ID, T2.NAME AS T2_NAME " +
                        "FROM TEAMS T INNER JOIN GAMES_RESULTS GR ON T.ID IN (" + String.join(", ", teamsIds) +
                        ") AND T.ID = GR.ID_TEAM INNER JOIN GAMES G ON G.ID = GR.ID_GAME INNER JOIN GAMES_RESULTS " +
                        "GR1 ON G.ID = GR1.ID_GAME INNER  JOIN TEAMS T2 ON T2.ID <> T.ID AND T2.ID = GR1.ID_TEAM",
                (rs, rowNum) -> {
                    Long gameId = rs.getLong("G_ID");
                    Game mapGame = gameMap.get(gameId);

                    if (!gameMap.containsKey(gameId)) {
                        mapGame = Game.builder()
                                .id(gameId)
                                .beginningDateTime(rs.getTimestamp("G_DATE_TIME_BEGIN").toLocalDateTime())
                                .isOver(rs.getInt("G_IS_OVER"))
                                .build();

                        gameMap.put(gameId, mapGame);
                    }

                    Long gameResult1Id = rs.getLong("GR1_ID");

                    if (!gameResultMap.containsKey(gameResult1Id)) {
                        gameResultMap.put(gameResult1Id, GameResult.builder()
                                .id(gameResult1Id)
                                .gameId(rs.getLong("GR1_ID_GAME"))
                                .teamId(rs.getLong("GR1_ID_TEAM"))
                                .teamScore(rs.getLong("GR1_SCORE"))
                                .team(Team.builder()
                                        .id(rs.getLong("T2_ID"))
                                        .name(rs.getString("T2_NAME"))
                                        .build())
                                .build());
                    }

                    Long gameResultId = rs.getLong("GR_ID");

                    if (!teamGameResultMap.containsKey(gameResultId)) {
                        teamGameResultMap.put(gameResultId, GameResult.builder()
                                .id(gameResultId)
                                .gameId(rs.getLong("GR_ID_GAME"))
                                .teamId(rs.getLong("GR_ID_TEAM"))
                                .teamScore(rs.getLong("GR_SCORE"))
                                .game(mapGame)
                                .build());
                    }

                    Long teamId = rs.getLong("T_ID");

                    if (!teamMap.containsKey(teamId)) {
                        teamMap.put(teamId, Team.builder()
                                .id(teamId)
                                .name(rs.getString("T_NAME"))
                                .build());
                    }

                    return null;
                });

        gameResultMap.forEach((key, value) -> gameMap.get(value.getGameId())
                .getGameResultList()
                .add(value));

        teamGameResultMap.forEach((key, value) -> teamMap.get(value.getTeamId())
                .getGameResultList()
                .add(value));

        return teamMap.values();
    }
}