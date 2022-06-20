package game;

import java.util.ArrayList;
import java.util.List;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.PlayerState;
import server.exceptions.GenericExampleException;
import server.exceptions.NumberOfPlayersException;
import server.exceptions.UniquePlayerIdException;

public class Game {
	private UniqueGameIdentifier gameID;
	private String gameStateID;
	private List<PlayerState> players;
	private List<HalfMap> playersHalfMaps;
	private FullMap fullMap;
	private int currentTurn;
	
	public Game(UniqueGameIdentifier gameID) {
		this.gameID = gameID;
		this.gameStateID = gameID.getUniqueGameID();
		this.players = new ArrayList<>();
		this.playersHalfMaps = new ArrayList<>();
		this.fullMap = new FullMap();
		this.currentTurn = 0;
	}
	
	public List<PlayerState> getPlayers() {
		return players;
	}
 	
	public void addPlayer(PlayerState player) {
		if(players.size() >= 2) {
			throw new NumberOfPlayersException("Number of Players check", "Game already has 2 registered players");
		}
		if(players.contains(player)) {
			throw new UniquePlayerIdException("Unique PlayerID check", "This player is already registered for the game");
		}
		players.add(player);
		currentTurn++;
	}
	
	public void addHalfMap(HalfMap halfMap) {
		
	}
}
