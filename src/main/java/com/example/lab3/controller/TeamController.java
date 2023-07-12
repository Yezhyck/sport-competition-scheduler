package com.example.lab3.controller;

import com.example.lab3.entity.Team;
import com.example.lab3.service.TeamServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@Tag(name = "Team controller", description = "Controller for teams management")
@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamServiceImpl teamService;

    @Autowired
    public TeamController(TeamServiceImpl teamService) {
        this.teamService = teamService;
    }

    @Operation(summary = "Creating team", description = "This operation saves a new team by generating a unique team" +
            " id on the server. If all team parameters are specified correctly, the operation returns the created" +
            " team in its full form, as well as the operation success code 200. If at least one team parameter" +
            " is specified in an invalid format, the code 400 is returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid team parameters", content = @Content)
    })
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Team> create(@Parameter(description = "Created team") @Valid @RequestBody Team team) {
        return ResponseEntity.ok(teamService.save(team));
    }

    @Operation(summary = "Getting team by id", description = "This operation finds an existing team with the given " +
            "id. If the team exists, the operation returns the complete team along with a success code of 200. If " +
            "the identifier is not well-formed, the return code is 400. If a team identifier that does not exist, " +
            "a 404 code and a corresponding message are returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Team> read(@Parameter(description = "Team id") @PathVariable Long id) {
        return ResponseEntity.of(teamService.findById(id));
    }

    @Operation(summary = "Updating team by id", description = "This operation updates an existing team with the " +
            "specified id. If all of the team parameters and the existing team id are correct, the operation returns" +
            " a fully updated team along with a success code of 200. If at least one of the team's team parameters is" +
            " incorrectly formatted, or a non-existent team id is specified, a 400 code is returned. If an error " +
            "occurs while creating the team, returns 404 code and corresponding message."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid id or team parameters supplied",
                    content = @Content)
    })
    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Team> update(@Parameter(description = "Team id") @PathVariable Long id,
                                       @Parameter(description = "Updated team") @Valid @RequestBody Team team) {
        team.setId(id);

        return ResponseEntity.ok(teamService.save(team));
    }

    @Operation(summary = "Deleting team by id", description = "This operation deletes the existing team by the " +
            "specified id. If an existing team id is specified in the correct format, the operation returns a success" +
            " code of 200. If a non-existent team id is specified and it is not in the correct format, a 400 code is" +
            " returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public void delete(@Parameter(description = "Team id") @PathVariable Long id) {
        teamService.deleteById(id);
    }

    @Operation(summary = "Getting all teams", description = "This operation finds existing teams. If the teams exist," +
            " the operation returns a list of all existing teams along with a success code of 200. If all teams are " +
            "missing, the return code is 404."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teams found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Team.class))
                    )
            })
    })
    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<Team>> readAll() {
        return ResponseEntity.ok(teamService.findAll());
    }

    @Operation(summary = "Getting teams by team name", description = "This operation finds all existing teams whose " +
            "whose names match or match the specified team name. If at least one such team exists and the team name " +
            "is in the correct format, the operation returns the corresponding list of complete teams along with a " +
            "success code of 200."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teams found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Team.class))
                    )
            })
    })
    @GetMapping("/name/{name}")
    @ResponseBody
    public ResponseEntity<Collection<Team>> readAllByName(@Parameter(description = "Team name or part of the team name")
                                                          @PathVariable String name) {
        return ResponseEntity.ok(teamService.findAllByName(name));
    }

    @Operation(summary = "Getting teams by page number", description = "This operation provides a fixed size list o " +
            "existing teams according to the specified page number. If the specified page number is in the correct " +
            "format, the operation returns a fixed-size list of existing teams according to the specified page " +
            "number, along with success code 200. If the page number is not in the correct format, the function " +
            "returns code 400."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teams found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Team.class))
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Invalid page number supplied", content = @Content)
    })
    @GetMapping("/page/{page}")
    @ResponseBody
    public ResponseEntity<Collection<Team>> readAllByPage(@Parameter(description = "Page number")
                                                          @PathVariable Long page) {
        return ResponseEntity.ok(teamService.findAllByPageAndSize(page, 4L));
    }

    @Operation(summary = "Getting teams by page number and size", description = "This operation provides a list of " +
            "existing teams of the specified size, according to the specified page number. If the specified page " +
            "number and size are in the correct format, the operation returns a list of existing teams of the " +
            "specified size, according to the specified page number, along with success code 200. If the page number " +
            "or size is not in the correct format, the function returns code 400."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teams found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Team.class))
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Invalid page number or size supplied", content = @Content)
    })
    @GetMapping("/page/{page}/size/{size}")
    @ResponseBody
    public ResponseEntity<Collection<Team>> readAllByPageAndSize(@Parameter(description = "Page number")
                                                                 @PathVariable Long page,
                                                                 @Parameter(description = "Page size")
                                                                 @PathVariable Long size) {
        return ResponseEntity.ok(teamService.findAllByPageAndSize(page, size));
    }
}