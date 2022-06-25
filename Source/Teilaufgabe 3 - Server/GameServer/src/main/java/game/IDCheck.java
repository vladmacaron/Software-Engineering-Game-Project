package game;

import java.util.Set;

import MessagesBase.MessagesFromServer.PlayerState;
import server.exceptions.GameIdException;
import server.exceptions.PlayerIdException;

public class IDCheck {
	public static void checkGameID(Set<String> gameIDs, String gameID) {
		if(gameIDs.contains(gameID)) {
			return;
		}
		throw new GameIdException("GameID check", "Cant find game with this gameID");
	}
	
	public static void checkPlayerID(Game game, String playerID) {
		for(PlayerState player: game.getPlayers()) {
			if(player.getUniquePlayerID().equals(playerID)) {
				return;
			}
		}
		throw new PlayerIdException("PlayerID check", "Cant find given PlayerID for this game");
	}
}
