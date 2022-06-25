package game;

import java.time.Duration;
import java.time.LocalTime;

import server.exceptions.NumberOfHalfMapsException;
import server.exceptions.NumberOfPlayersException;
import server.exceptions.NumberOfTurnsException;
import server.exceptions.TimeRuleException;
import server.exceptions.TurnBasedException;
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
		if(currentTurn >= 200) {
			throw new NumberOfTurnsException("Number of Turns check", "Number of moves cannot be more than 200");
		}
	}
	
	public static void checkNumberOfPlayers(Game game) {
		if(game.getPlayers().size() >= 2) {
			throw new NumberOfPlayersException("Number of Players check", "Game already has 2 registered players");
		}
	}
	
	public static void checkNumberOfHalfMaps(Game game) {
		if(game.getPlayersHalfMaps().size() == 2) {
			throw new NumberOfHalfMapsException("Number of HalfMaps check", "Game already received 2 HalfMaps");
		}
	}
	
	public static void checkTurn(Game game, String playerID) {
		if(!game.getCurrentPlayerID().equals(playerID)) {
			game.setEndGame(playerID);
			throw new TurnBasedException("Turn Based check", "You are not allowed to make a turn while your state is MustWait");
		}
	}
}
