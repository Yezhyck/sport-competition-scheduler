package com.example.lab2.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Game {
    public static final Integer MIN_TEAMS_AMOUNT = 2;
    public static final Integer MAX_TEAMS_AMOUNT = 4;

    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "Field cannot be empty")
    private LocalDateTime beginningDateTime;
    private Integer isOver;
    @Builder.Default
    private List<GameResult> gameResultList = new ArrayList<>();
}