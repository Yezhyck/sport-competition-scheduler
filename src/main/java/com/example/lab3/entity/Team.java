package com.example.lab3.entity;

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
@ToString
public class Team {
    private Long id;
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 18, message = "Name should be between 3 and 18 characters")
    private String name;
    @Builder.Default
    private List<GameResult> gameResultList = new ArrayList<>();
}