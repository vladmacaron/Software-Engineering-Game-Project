package game;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.PlayerState;
import map.HalfMapRules;
import server.exceptions.GenericExampleException;
import server.exceptions.NumberOfPlayersException;
import server.exceptions.UniquePlayerIdException;

public class Game {
	private UniqueGameIdentifier gameID;
	private String gameStateID;
	private Set<PlayerState> players;
	private List<HalfMap> playersHalfMaps;
	private FullMap fullMap;
	private int currentTurn;
	private LocalTime lastTurnTime;
	
	public Game(UniqueGameIdentifier gameID) {
		this.gameID = gameID;
		this.gameStateID = gameID.getUniqueGameID();
		this.players = new HashSet<>();
		this.playersHalfMaps = new ArrayList<>();
		this.fullMap = new FullMap();
		this.currentTurn = 0;
		this.lastTurnTime = null;
	}
	
	public Set<PlayerState> getPlayers() {
		return players;
	}
	
	public LocalTime getLastTurnTime() {
		return lastTurnTime;
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
		//HalfMapRules.checkHalfMap(halfMap);
		
		playersHalfMaps.add(halfMap);
		currentTurn++;
		lastTurnTime = LocalTime.now();
	}
}
