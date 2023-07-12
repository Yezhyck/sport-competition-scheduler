package com.example.lab2.controller;

import com.example.lab2.entity.Game;
import com.example.lab2.service.GameResultServiceImpl;
import com.example.lab2.service.GameServiceImpl;
import com.example.lab2.service.TeamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class GameController {

    @Autowired
    private TeamServiceImpl teamService;
    private GameServiceImpl gameService;
    private final GameResultServiceImpl gameResultService;

    @Autowired
    public void setGameService(GameServiceImpl gameService) {
        this.gameService = gameService;
    }

    @Autowired
    public GameController(GameResultServiceImpl gameResultService) {
        this.gameResultService = gameResultService;
    }

    @GetMapping("/games/create")
    public String create(Integer teamsAmount, Model model) {
        model.addAttribute("allTeams", teamService.findAll());
        model.addAttribute("teamsAmount", teamsAmount != null ? teamsAmount : Game.MIN_TEAMS_AMOUNT);
        model.addAttribute("game", new Game());

        return "/create-game";
    }

    @PostMapping("/games/create")
    public String create(@Valid Game game, BindingResult bindingResult,
                         @RequestParam("teamsIds") List<Long> teamsIds,
                         @RequestParam("teamsScores") List<Long> teamsScores,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTeams", teamService.findAll());
            model.addAttribute("teamsIdsInvalid", teamsIds);
            model.addAttribute("teamsScoresInvalid", teamsScores);
            model.addAttribute("teamsAmount", teamsIds.size());
            model.addAttribute("game", game);

            return "/create-game";
        }

        game.setGameResultList(gameResultService.convertToGamesResultsList(teamsIds, teamsScores));

        gameService.save(game);

        return "redirect:/games";
    }

    @GetMapping("/games")
    public String readAll(Model model) {
        model.addAttribute("allGames", gameService.findAll());
        model.addAttribute("allTeams", teamService.findAll());
        model.addAttribute("minTeamsAmount", Game.MIN_TEAMS_AMOUNT);
        model.addAttribute("maxTeamsAmount", Game.MAX_TEAMS_AMOUNT);

        return "/show-games";
    }

    @GetMapping("/games/byTeamName")
    public String readAllByTeamName(String keyword, Model model) {
        model.addAttribute("allGames", gameService.findAllByTeamName(keyword));
        model.addAttribute("allTeams", teamService.findAll());
        model.addAttribute("minTeamsAmount", Game.MIN_TEAMS_AMOUNT);
        model.addAttribute("maxTeamsAmount", Game.MAX_TEAMS_AMOUNT);
        model.addAttribute("keyword", keyword);

        return "/show-games";
    }

    @GetMapping("/games/update/{id}")
    public String update(@PathVariable Long id, Integer teamsAmount, Model model) {
        Optional<Game> game = gameService.findById(id);

        if (game.isPresent()) {
            model.addAttribute("allTeams", teamService.findAll());
            model.addAttribute("teamsAmount", teamsAmount != null ? teamsAmount :
                    game.get().getGameResultList().size());
            model.addAttribute("game", game.get());
        }

        return "/update-game";
    }

    @PostMapping("/games/update/{id}")
    public String update(@PathVariable Long id, @Valid Game game, BindingResult bindingResult,
                         @RequestParam("teamsIds") List<Long> teamsIds,
                         @RequestParam("teamsScores") List<Long> teamsScores,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTeams", teamService.findAll());
            model.addAttribute("teamsIdsInvalid", teamsIds);
            model.addAttribute("teamsScoresInvalid", teamsScores);
            model.addAttribute("teamsAmount", teamsIds.size());
            model.addAttribute("game", game);

            return "/update-game";
        }

        game.setId(id);
        game.setGameResultList(gameResultService.convertToGamesResultsList(teamsIds, teamsScores));

        gameService.save(game);

        return "redirect:/games";
    }

    @GetMapping("/games/delete/{id}")
    public String delete(@PathVariable Long id) {
        gameService.deleteById(id);

        return "redirect:/games";
    }
}