package com.example.lab2.controller;

import com.example.lab2.entity.Team;
import com.example.lab2.service.TeamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class TeamController {
    private final TeamServiceImpl teamService;

    @Autowired
    public TeamController(TeamServiceImpl teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/teams/create")
    public String create(Model model) {
        model.addAttribute("team", new Team());

        return "/create-team";
    }

    @PostMapping("/teams/create")
    public String create(@Valid Team team, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/create-team";
        }

        teamService.save(team);

        return "redirect:/teams";
    }

    @GetMapping("/teams/{id}")
    public String read(@PathVariable Long id, Model model) {
        teamService.findById(id)
                .ifPresent(value -> model.addAttribute("team", value));

        return "/show-team";
    }

    @GetMapping("/teams")
    public String readAll(Model model) {
        model.addAttribute("allTeams", teamService.findAll());

        return "/show-teams";
    }

    @GetMapping("/teams/byName")
    public String readAllByName(String keyword, Model model) {
        model.addAttribute("allTeams", teamService.findAllByName(keyword));
        model.addAttribute("keyword", keyword);

        return "/show-teams";
    }

    @GetMapping("/teams/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        teamService.findById(id)
                .ifPresent(value -> model.addAttribute("team", value));

        return "/update-team";
    }

    @PostMapping("/teams/update/{id}")
    public String update(@PathVariable Long id, @Valid Team team, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/update-team";
        }

        team.setId(id);

        teamService.save(team);

        return "redirect:/teams";
    }

    @GetMapping("/teams/delete/{id}")
    public String delete(@PathVariable Long id) {
        teamService.deleteById(id);

        return "redirect:/teams";
    }
}