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
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class GameRepositoryImpl implements GameRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Game> gameRowMapper;

    public GameRepositoryImpl(DataSource dataSource, RowMapper<Game> gameRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.gameRowMapper = gameRowMapper;
    }

    @Override
    public Game save(Game entity) {
        Optional<Game> optionalGame = findById(entity.getId());

        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            List<GameResult> existGameResultList = game.getGameResultList();
            List<GameResult> entityGameResultList = entity.getGameResultList();
            StringBuilder updateGamesResultsQuery = new StringBuilder();

            if (entityGameResultList.size() < existGameResultList.size()) {
                int difference = existGameResultList.size() - entityGameResultList.size();

                for (int i = 0; i < entityGameResultList.size(); i++) {
                    updateGamesResultsQuery
                            .append("SELECT ")
                            .append(existGameResultList.get(i + difference).getId())
                            .append(" AS ID, ")
                            .append(game.getId())
                            .append(" AS ID_GAME, ")
                            .append(entityGameResultList.get(i).getTeamId())
                            .append(" AS ID_TEAM, ")
                            .append(entityGameResultList.get(i).getTeamScore())
                            .append(" AS SCORE FROM DUAL");

                    if (i != entityGameResultList.size() - 1) {
                        updateGamesResultsQuery.append(" UNION ALL ");
                    }
                }

                StringBuilder deleteAllGamesResultsQuery = new StringBuilder(
                        "DELETE FROM GAMES_RESULTS GR WHERE GR.ID IN ("
                );

                for (int i = 0; i < difference; i++) {
                    deleteAllGamesResultsQuery.append(existGameResultList.get(i).getId());

                    if (i != difference - 1) {
                        deleteAllGamesResultsQuery.append(", ");
                    }
                }

                deleteAllGamesResultsQuery.append(")");

                jdbcTemplate.update(deleteAllGamesResultsQuery.toString());
            } else {
                Long gameResultId;

                for (int i = 0; i < entityGameResultList.size(); i++) {
                    gameResultId = i < existGameResultList.size() ? existGameResultList.get(i).getId() : null;

                    updateGamesResultsQuery
                            .append("SELECT ")
                            .append(gameResultId)
                            .append(" AS ID, ")
                            .append(game.getId())
                            .append(" AS ID_GAME, ")
                            .append(entityGameResultList.get(i).getTeamId())
                            .append(" AS ID_TEAM, ")
                            .append(entityGameResultList.get(i).getTeamScore())
                            .append(" AS SCORE FROM DUAL");

                    if (i != entityGameResultList.size() - 1) {
                        updateGamesResultsQuery.append(" UNION ALL ");
                    }
                }
            }

            if (updateGamesResultsQuery.length() > 0) {
                updateGamesResultsQuery
                        .insert(0, "MERGE INTO GAMES_RESULTS GR USING (")
                        .append(") SRC ON (GR.ID = SRC.ID) ")
                        .append("WHEN MATCHED THEN ")
                        .append("UPDATE SET GR.ID_GAME = SRC.ID_GAME, GR.ID_TEAM = SRC.ID_TEAM, GR.SCORE = SRC.SCORE ")
                        .append("WHEN NOT MATCHED THEN ")
                        .append("INSERT VALUES (SRC.ID, SRC.ID_GAME, SRC.ID_TEAM, SRC.SCORE)");

                jdbcTemplate.update(updateGamesResultsQuery.toString());
            }

            jdbcTemplate.update("UPDATE GAMES G SET G.DATE_TIME_BEGIN = ?, G.IS_OVER = ? WHERE G.ID = ?",
                    entity.getBeginningDateTime(), entity.getIsOver(), entity.getId());
        } else {
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(con -> {
                PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO GAMES " +
                        "(DATE_TIME_BEGIN, IS_OVER) VALUES (?, ?)", new String[]{"ID"});
                preparedStatement.setTimestamp(1, Timestamp.valueOf(entity.getBeginningDateTime()));
                preparedStatement.setInt(2, entity.getIsOver());

                return preparedStatement;
            }, generatedKeyHolder);

            Long gameId = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();

            entity.setId(gameId);

            List<GameResult> gameResultList = entity.getGameResultList();

            if (!gameResultList.isEmpty()) {
                StringBuilder gamesResultsQuery = new StringBuilder("INSERT ALL ");

                gameResultList.forEach(gameResult -> {
                    gameResult.setGameId(gameId);

                    gamesResultsQuery
                            .append("INTO GAMES_RESULTS (ID_GAME, ID_TEAM, SCORE) VALUES (")
                            .append(gameId)
                            .append(", ")
                            .append(gameResult.getTeamId())
                            .append(", ")
                            .append(gameResult.getTeamScore())
                            .append(") ");
                });

                gamesResultsQuery.append("SELECT 1 FROM DUAL");

                jdbcTemplate.update(gamesResultsQuery.toString());
            }
        }

        return findById(entity.getId()).orElse(entity);
    }

    @Override
    public Optional<Game> findById(Long id) {
        Optional<Game> game = jdbcTemplate.query("SELECT * FROM GAMES G WHERE G.ID = ?", gameRowMapper, id)
                .stream().findAny();

        if (game.isEmpty()) {
            return game;
        }

        List<GameResult> gameResultList = jdbcTemplate.query("SELECT GR.ID AS GR_ID, GR.ID_GAME AS GR_ID_GAME, " +
                        "GR.ID_TEAM AS GR_ID_TEAM, GR.SCORE AS GR_SCORE, T.ID AS T_ID, T.NAME AS T_NAME FROM " +
                        "GAMES_RESULTS GR INNER JOIN TEAMS T ON GR.ID_GAME = ? AND T.ID = GR.ID_TEAM",
                (rs, rowNum) -> GameResult.builder()
                        .id(rs.getLong("GR_ID"))
                        .gameId(rs.getLong("GR_ID_GAME"))
                        .teamId(rs.getLong("GR_ID_TEAM"))
                        .teamScore(rs.getLong("GR_SCORE"))
                        .team(Team.builder()
                                .id(rs.getLong("T_ID"))
                                .name(rs.getString("T_NAME"))
                                .build())
                        .build(), id);

        game.get().setGameResultList(gameResultList);

        return game;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM GAMES G WHERE G.ID = ?", id);
    }

    @Override
    public Collection<Game> findAll() {
        Map<Long, Game> gameMap = new LinkedHashMap<>();
        Map<Long, GameResult> gameResultMap = new LinkedHashMap<>();

        jdbcTemplate.query("SELECT G.ID AS G_ID, G.DATE_TIME_BEGIN AS G_DATE_TIME_BEGIN, " +
                        "G.IS_OVER AS G_IS_OVER, T.ID AS T_ID, T.NAME AS T_NAME, GR.ID AS GR_ID, " +
                        "GR.ID_GAME AS GR_ID_GAME, GR.ID_TEAM AS GR_ID_TEAM, GR.SCORE AS GR_SCORE " +
                        "FROM GAMES G INNER JOIN GAMES_RESULTS GR ON G.ID = GR.ID_GAME INNER JOIN " +
                        "TEAMS T ON T.ID = GR.ID_TEAM",
                (rs, rowNum) -> {
                    Long gameId = rs.getLong("G_ID");

                    if (!gameMap.containsKey(gameId)) {
                        gameMap.put(gameId, Game.builder()
                                .id(gameId)
                                .beginningDateTime(rs.getTimestamp("G_DATE_TIME_BEGIN").toLocalDateTime())
                                .isOver(rs.getInt("G_IS_OVER"))
                                .build());
                    }

                    Long gameResultId = rs.getLong("GR_ID");

                    gameResultMap.put(gameResultId, GameResult.builder()
                            .id(gameResultId)
                            .gameId(rs.getLong("GR_ID_GAME"))
                            .teamId(rs.getLong("GR_ID_TEAM"))
                            .teamScore(rs.getLong("GR_SCORE"))
                            .team(Team.builder()
                                    .id(rs.getLong("T_ID"))
                                    .name(rs.getString("T_NAME"))
                                    .build())
                            .build());

                    return null;
                });

        gameResultMap.forEach((key, value) -> gameMap.get(value.getGameId())
                .getGameResultList()
                .add(value));

        return List.copyOf(gameMap.values());
    }

    @Override
    public Collection<Game> findAllByTeamName(String name) {
        return findAll().stream()
                .filter(game -> game.getGameResultList().stream()
                        .anyMatch(gameResult -> gameResult.getTeam().getName().contains(name)))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Game> findAllByPageAndSize(Long page, Long size) {
        if (page <= 0 || size <= 0) {
            return new ArrayList<>();
        }

        long start = (page - 1) * size + 1;
        long end = start + size - 1;

        List<String> gamesIds = jdbcTemplate.query("SELECT * FROM (SELECT G.ID AS G_ID, row_number() over " +
                                "(ORDER BY G.ID) line_number FROM GAMES G) WHERE line_number BETWEEN " + start + " AND " + end,
                        (rs, rowNum) -> rs.getLong("G_ID")).stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        if (gamesIds.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Game> gameMap = new LinkedHashMap<>();
        Map<Long, GameResult> gameResultMap = new LinkedHashMap<>();

        jdbcTemplate.query("SELECT G.ID AS G_ID, G.DATE_TIME_BEGIN AS G_DATE_TIME_BEGIN, " +
                        "G.IS_OVER AS G_IS_OVER, T.ID AS T_ID, T.NAME AS T_NAME, GR.ID AS GR_ID, " +
                        "GR.ID_GAME AS GR_ID_GAME, GR.ID_TEAM AS GR_ID_TEAM, GR.SCORE AS GR_SCORE " +
                        "FROM GAMES G INNER JOIN GAMES_RESULTS GR ON G.ID = GR.ID_GAME AND G.ID IN (" +
                        String.join(", ", gamesIds) + ") INNER JOIN " + "TEAMS T ON T.ID = GR.ID_TEAM",
                (rs, rowNum) -> {
                    Long gameId = rs.getLong("G_ID");

                    if (!gameMap.containsKey(gameId)) {
                        gameMap.put(gameId, Game.builder()
                                .id(gameId)
                                .beginningDateTime(rs.getTimestamp("G_DATE_TIME_BEGIN").toLocalDateTime())
                                .isOver(rs.getInt("G_IS_OVER"))
                                .build());
                    }

                    Long gameResultId = rs.getLong("GR_ID");

                    gameResultMap.put(gameResultId, GameResult.builder()
                            .id(gameResultId)
                            .gameId(rs.getLong("GR_ID_GAME"))
                            .teamId(rs.getLong("GR_ID_TEAM"))
                            .teamScore(rs.getLong("GR_SCORE"))
                            .team(Team.builder()
                                    .id(rs.getLong("T_ID"))
                                    .name(rs.getString("T_NAME"))
                                    .build())
                            .build());

                    return null;
                });

        gameResultMap.forEach((key, value) -> gameMap.get(value.getGameId())
                .getGameResultList()
                .add(value));

        return List.copyOf(gameMap.values());
    }
}