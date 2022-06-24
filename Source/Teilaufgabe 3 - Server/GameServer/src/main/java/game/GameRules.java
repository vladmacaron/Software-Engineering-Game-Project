package game;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

import server.exceptions.NumberOfPlayersException;
import server.exceptions.NumberOfTurnsException;
import server.exceptions.TimeRuleException;
import server.exceptions.TwoPlayersException;

public class GameRules {
	public static void checkBothPlayersAreRegistered(Game game) {
		if(game.getPlayers().size()!=2) {
			throw new TwoPlayersException("Two Players check", "Not both players are registered for the game");
		}
	}
	
	public static void checkTimeRule(LocalTime lastTurnTime) {
		if(Duration.between(lastTurnTime, LocalTime.now()).getSeconds()>3) {
			throw new TimeRuleException("Time Rule check", "More than 3 seconds passed between actions");
		}
	}
	
	public static void checkNumberOfTurns(int currentTurn) {
		if(currentTurn > 200) {
			throw new NumberOfTurnsException("Number of Turns check", "Number of moves cannot be more than 200");
		}
	}
	
	public static void checkNumberOfPlayers(Game game) {
		if(game.getPlayers().size() >= 2) {
			throw new NumberOfPlayersException("Number of Players check", "Game already has 2 registered players");
		}
	}
}
