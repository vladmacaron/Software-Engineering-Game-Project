
# Project

The goal of this project was to make game client and game server.

The basic game idea for each client/AI to find a treasure and bring it to to the opponent castle. The AI that completes them faster wins the game; which is turn-based (therefore each AI takes turns taking a game action). The map on which the game is played is not fixed, but is created independently and automatically by both AIs when the game starts (one half of the map each).

# Technology

Programmiersprache:	Java 17 (1.17)
Entwicklungsumgebung:	Eclipse 2021‑12 for Java Developers (siehe auch den Hinweis unten)
UI-Framework:	Client: Voraussetzung CLI für automatisierte Tests. 
Netzwerkkommunikation:	Spring Boot 2.5.5, Spring Webflux 2.5.5, JAXB Reference Implementation 2.3.X, iStack 4.0.1
Logging:	Logback 1.2.6 mit SLF4J 1.7.32
Unit Testing:	JUnit 5.8.1 und Mockito 3.12.4
Sourcecode: Verwaltung	GitLab der Universität
Build Management Tools: Gradle
Datenbankeinbindung:	OR Mapper für Management und Zugriff (Hibernate via Spring Data JPA 2.5.5), SQLite JDBC via Xerial SQLite JDBC 3.36.0.3
