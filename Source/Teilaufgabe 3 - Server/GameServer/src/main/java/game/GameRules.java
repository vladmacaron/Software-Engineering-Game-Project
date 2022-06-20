package game;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

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
}
