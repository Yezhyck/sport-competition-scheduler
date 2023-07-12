# Sport Competition Scheduler

The "Sport Competition Scheduler" project is a Spring Boot Maven application developed for creating schedules for sport competitions. It provides functionality to manage teams, games, and game results. The project is implemented as a Spring MVC web application using Thymeleaf as the templating engine.

## Project Description

The project utilizes Spring Boot and Maven as the framework and build tool, respectively. It uses the following
dependencies:

- **Spring Boot Starter Thymeleaf**: Starter for building Spring MVC web applications with Thymeleaf as the templating engine.
- **Spring Boot Starter Web**: Starter for building web applications using Spring MVC.
- **Spring Boot Starter Test**: Starter for testing Spring Boot applications.
- **Spring Boot Starter JDBC**: Starter for using JDBC with the Spring Framework.
- **ojdbc8**: Oracle JDBC driver for connecting to an Oracle database.
- **Lombok**: Library for reducing boilerplate code in Java classes.
- **Spring Boot Starter Validation**: Starter for using validation with Spring Boot.

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

## MVC Architecture

The project implements MVC architecture with the following controllers:

### TeamController

- GET ```/teams/create```:

![Create Team Page](/images/teams-create.png)

- GET ```/teams/{id}```: 

![Read Team By Id Page](/images/teams-read-by-id.png)

- GET ```/teams```: 

![Read All Teams Page](/images/teams-read-all.png)

- GET ```/teams/byName```: 

![Read Team By Name Page](/images/teams-read-by-name.png)

- GET ```/teams/update/{id}```:

![Update Team By Id Page](/images/teams-update-by-id.png)

### GameController

- GET ```/games/create```:

![Create Game Page](/images/games-create.png)

- GET ```/games```: 

![Read All Games Page](/images/games-read-all.png)

- GET ```/games/byTeamName```: 

![Read All Games By Team Name Page](/images/games-read-by-team-name.png)

- GET ```/games/update/{id}```:

![Update Game By Id Page](/images/games-update-by-id.png)