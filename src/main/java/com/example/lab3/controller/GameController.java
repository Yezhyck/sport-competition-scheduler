package com.example.lab3.controller;

import com.example.lab3.entity.Game;
import com.example.lab3.service.GameServiceImpl;
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

@Tag(name = "Game controller", description = "Controller for games management")
@RestController
@RequestMapping("/games")
public class GameController {
    private final GameServiceImpl gameService;

    @Autowired
    public GameController(GameServiceImpl gameService) {
        this.gameService = gameService;
    }

    @Operation(summary = "Creating game", description = "This operation saves a new game by generating a unique game" +
            " id on the server. If all game parameters are specified correctly, the operation returns the created" +
            " game in its full form, as well as the operation success code 200. If at least one game parameter" +
            " is specified in an invalid format, the code 400 is returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Game.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid game parameters", content = @Content)
    })
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Game> create(@Parameter(description = "Created game") @Valid @RequestBody Game game) {
        return ResponseEntity.ok(gameService.save(game));
    }

    @Operation(summary = "Getting game by id", description = "This operation finds an existing game with the given " +
            "id. If the game exists, the operation returns the complete game along with a success code of 200. If " +
            "the identifier is not well-formed, the return code is 400. If a game identifier that does not exist, " +
            "a 404 code and a corresponding message are returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Game.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Game not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Game> read(@Parameter(description = "Game id") @PathVariable Long id) {
        return ResponseEntity.of(gameService.findById(id));
    }

    @Operation(summary = "Updating game by id", description = "This operation updates an existing game with the " +
            "specified id. If all of the game parameters and the existing game id are correct, the operation returns" +
            " a fully updated game along with a success code of 200. If at least one of the game's game parameters is" +
            " incorrectly formatted, or a non-existent game ID is specified, a 400 code is returned. If an error " +
            "occurs while creating the game, returns 404 code and corresponding message."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Game.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid id or game parameters supplied",
                    content = @Content)
    })
    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Game> update(@Parameter(description = "Game id") @PathVariable Long id,
                                       @Parameter(description = "Updated game") @Valid @RequestBody Game game) {
        game.setId(id);

        return ResponseEntity.ok(gameService.save(game));
    }

    @Operation(summary = "Deleting game by id", description = "This operation deletes the existing game by the " +
            "specified id. If an existing game id is specified in the correct format, the operation returns a success" +
            " code of 200. If a non-existent game id is specified and it is not in the correct format, a 400 code is" +
            " returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public void delete(@Parameter(description = "Game id") @PathVariable Long id) {
        gameService.deleteById(id);
    }

    @Operation(summary = "Getting all games", description = "This operation finds existing games. If the games exist," +
            " the operation returns a list of all existing games along with a success code of 200. If all games are " +
            "missing, the return code is 404."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Games found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Game.class))
                    )
            })
    })
    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<Game>> readAll() {
        return ResponseEntity.ok(gameService.findAll());
    }

    @Operation(summary = "Getting games by team name", description = "This operation finds all existing games that " +
            "have teams whose names match or match the specified team name. If at least one such game exists and the" +
            " team name is in the correct format, the operation returns the corresponding list of complete games" +
            " along with a success code of 200."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Games found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Game.class))
                    )
            })
    })
    @GetMapping("/team-name/{teamName}")
    @ResponseBody
    public ResponseEntity<Collection<Game>> readAllByTeamName(
            @Parameter(description = "Team name or part of the team name")
            @PathVariable String teamName) {
        return ResponseEntity.ok(gameService.findAllByTeamName(teamName));
    }

    @Operation(summary = "Getting games by page number", description = "This operation provides a fixed size list o " +
            " existing games according to the specified page number. If the specified page number is in the correct " +
            "format, the operation returns a fixed-size list of existing games according to the specified page " +
            "number, along with success code 200. If the page number is not in the correct format, the function " +
            "returns code 400."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Games found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Game.class))
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Invalid page number supplied", content = @Content)
    })
    @GetMapping("/page/{page}")
    @ResponseBody
    public ResponseEntity<Collection<Game>> findAllByPage(@Parameter(description = "Page number")
                                                          @PathVariable Long page) {
        return ResponseEntity.ok(gameService.findAllByPageAndSize(page, 4L));
    }

    @Operation(summary = "Getting games by page number and size", description = "This operation provides a list of" +
            " existing games of the specified size, according to the specified page number. If the specified page" +
            " number and size are in the correct format, the operation returns a list of existing games of the" +
            " specified size, according to the specified page number, along with success code 200. If the page number" +
            " or size is not in the correct format, the function returns code 400."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Games found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Game.class))
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Invalid page number or size supplied", content = @Content)
    })
    @GetMapping("/page/{page}/size/{size}")
    @ResponseBody
    public ResponseEntity<Collection<Game>> findAllByPageAndSize(@Parameter(description = "Page number")
                                                                 @PathVariable Long page,
                                                                 @Parameter(description = "Page size")
                                                                 @PathVariable Long size) {
        return ResponseEntity.ok(gameService.findAllByPageAndSize(page, size));
    }
}