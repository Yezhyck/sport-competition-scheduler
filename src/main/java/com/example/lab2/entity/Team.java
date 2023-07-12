package com.example.lab2.entity;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Team {
    private Long id;
    @NotBlank(message = "Field cannot be empty")
    @Size(min = 3, max = 18, message = "Team name should be between 3 and 18 characters")
    private String name;
    @Builder.Default
    private List<GameResult> gameResultList = new ArrayList<>();
}