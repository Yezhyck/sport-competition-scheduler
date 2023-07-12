# Sport Competition Scheduler

Sport Competition Scheduler is a Spring Boot Maven project developed for creating schedules for sport competitions. The
project includes three entities: Game, Team, and GameResult. It provides a database schema consisting of three tables:
teams, games, and game_results.

## Project Description

The project utilizes Spring Boot and Maven as the framework and build tool, respectively. It uses the following
dependencies:

- spring-boot-starter-web
- lombok
- spring-boot-starter-test
- spring-boot-starter-jdbc
- ojdbc8
- spring-boot-starter-validation
- springdoc-openapi-ui
- validation-api
- hibernate-validator

The project's database schema consists of three tables:

- **Team**: Represents a team participating in the sport competitions. It has an ID and a name.
- **Game**: Represents a game in the competition. It has an ID, a start date and time, and a game over indicator.
- **GameResult**: Represents the result of a game. It has an ID, the ID of the game, the ID of the team, and the team's score in that game. The GameResult entity acts as a connection between teams and games.

The relationships between the entities are as follows:

- The relation between Team entity and GameResult entity is 1 to Many (1:M).
- The relation between Game entity and GameResult entity is 1 to Many (1:M).

## Database Schema Visualization

The image below provides a visualization of the database schema:

![Database Schema](/images/sport_competition_scheduler_db.png)

## REST API Architecture

The project implements a REST API architecture with the following controllers:

### TeamController

- POST ```/teams/create```: Create a new team.
- GET ```/teams/{id}```: Get a team by the specified ID.
- PUT ```/teams/update/{id}```: Update a team by the specified ID.
- DELETE ```/teams/delete/{id}```: Delete a team by the specified ID.
- GET ```/teams```: Get all teams.
- DELETE ```/teams/name/{name}```: Delete a team by the specified name.
- GET ```/teams/page/{page}```: Get all teams on the page with the specified number.
- GET ```/teams/page/{page}/size/{size}```: Get the specified number of teams on the page with the specified number.

### GameController

- POST ```/games/create```: Create a new game.
- GET ```/games/{id}```: Get a game by the specified ID.
- PUT ```/games/update/{id}```: Update a game by the specified ID.
- DELETE ```/games/delete/{id}```: Delete a game by the specified ID.
- GET ```/games```: Get all games.
- GET ```/games/team-name/{teamName}```: Get all games in which the team with the specified teamName took part.
- GET ```/games/page/{page}```: Get all games on the page with the specified number.
- GET ```/games/page/{page}/size/{size}```: Get the specified number of games on the page with the specified number.