package game;

import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromServer.EFortState;
import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.EPlayerPositionState;
import MessagesBase.MessagesFromServer.ETreasureState;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.FullMapNode;
import MessagesBase.MessagesFromServer.GameState;
import MessagesBase.MessagesFromServer.PlayerState;
import map.FullMapCreator;
import map.HalfMapRules;
import server.exceptions.GenericExampleException;
import server.exceptions.NumberOfHalfMapsException;
import server.exceptions.NumberOfPlayersException;
import server.exceptions.NumberOfTurnsException;
import server.exceptions.TurnBasedException;
import server.exceptions.UniquePlayerIdException;

public class Game {
	private UniqueGameIdentifier gameID;
	private String gameStateID;
	private List<PlayerState> players;
	private List<HalfMap> playersHalfMaps;
	private FullMap fullMap;
	private int currentTurn;
	private LocalTime lastTurnTime;
	private String currentPlayerID;
	
	public Game(UniqueGameIdentifier gameID) {
		this.gameID = gameID;
		this.gameStateID = gameID.getUniqueGameID();
		this.players = new ArrayList<>();
		this.playersHalfMaps = new ArrayList<>();
		this.fullMap = new FullMap();
		this.currentTurn = 0;
		this.lastTurnTime = null;
		this.currentPlayerID = "";
	}
	
	public List<PlayerState> getPlayers() {
		return players;
	}
	
	public LocalTime getLastTurnTime() {
		return lastTurnTime;
	}
	
	public int getCurrentTurn() {
		return currentTurn;
	}
 	
	public void addPlayer(PlayerState player) {
		if(players.contains(player)) {
			throw new UniquePlayerIdException("Unique PlayerID check", "This player is already registered for the game");
		}
		players.add(player);
		currentTurn++;
		gameStateID = gameStateID+1;
		
		if(players.size()==2 && currentPlayerID.isBlank()) {
			List<PlayerState> tempPlayers = new ArrayList<>();
			int firstPlayerIndex = new Random().nextInt(2);
			int secondPlayerIndex = 0;
			if(firstPlayerIndex==0) {
				secondPlayerIndex = 1;
			}
			PlayerState firstPlayer = players.get(firstPlayerIndex);
			PlayerState secondPlayer = players.get(secondPlayerIndex);
			tempPlayers.add(new PlayerState(firstPlayer.getFirstName(),
											firstPlayer.getLastName(),
											firstPlayer.getUAccount(),
											EPlayerGameState.MustAct,
											new UniquePlayerIdentifier(firstPlayer.getUniquePlayerID()),
											firstPlayer.hasCollectedTreasure()));
			tempPlayers.add(new PlayerState(secondPlayer.getFirstName(),
											secondPlayer.getLastName(),
											secondPlayer.getUAccount(),
											EPlayerGameState.MustWait,
											new UniquePlayerIdentifier(secondPlayer.getUniquePlayerID()),
											secondPlayer.hasCollectedTreasure()));
			currentPlayerID = firstPlayer.getUniquePlayerID();
			players = tempPlayers;
		}
	}
	
	public void addHalfMap(HalfMap halfMap) {
		System.out.println("add half map entry");
		if(playersHalfMaps.size() >= 2) {
			throw new NumberOfHalfMapsException("Number of HalfMaps check", "Game already received 2 HalfMaps");
		}
		for(HalfMap checkHalfMap: playersHalfMaps) {
			if(checkHalfMap.getUniquePlayerID().equals(halfMap.getUniquePlayerID())) {
				setEndGame(halfMap.getUniquePlayerID());
				throw new NumberOfHalfMapsException("Number of HalfMaps check", "Player cannot send HalfMap more than 1 time");
			}
		}
		if(!currentPlayerID.equals(halfMap.getUniquePlayerID())) {
			setEndGame(halfMap.getUniquePlayerID());
			throw new TurnBasedException("Turn Based check", "This is not your turn");
		}
		
		System.out.println(playersHalfMaps.size());
		System.out.println("GameID: " + gameID + " PlayerID: " + currentPlayerID);
		
		changeCurrentPlayerID();
		switchPlayers();
		
		playersHalfMaps.add(halfMap);
		
		System.out.println("GameID: " + gameID + " PlayerID: " + currentPlayerID);
		System.out.println(playersHalfMaps.size());
		
		if(playersHalfMaps.size() == 2) {
			AbstractMap.SimpleEntry<FullMap, String> fullMapAndPlayerIDPair = FullMapCreator.createFullMap(playersHalfMaps);
			fullMap = fullMapAndPlayerIDPair.getKey();
			//currentPlayerID = fullMapAndPlayerIDPair.getValue();
		}
		
		currentTurn++;
		gameStateID = gameStateID+1;
		
		lastTurnTime = LocalTime.now();
	}
	
	private void changeCurrentPlayerID() {
		for(PlayerState player: players) {
			if(!currentPlayerID.equals(player.getUniquePlayerID())) {
				currentPlayerID = player.getUniquePlayerID();
				break;
			}
		}
	}
	
	public GameState getGameState(UniquePlayerIdentifier playerID) {
		if(players.size()<1) {
			return new GameState(gameStateID);
		}
		if(players.size()<=2 && playersHalfMaps.size()<2) {
			System.out.println("Check " + currentPlayerID);
			//return new GameState(players, gameStateID);
			return new GameState(hidePlayerID(playerID.getUniquePlayerID()), gameStateID);
		}
		
		boolean shouldSwitchMap = false;
		
		if(!currentPlayerID.equals(playerID.getUniquePlayerID())) {
			shouldSwitchMap = true;
		}
		
		if(shouldSwitchMap) {
			fullMap = changeMapForPlayer();
		}
		
		changeCurrentPlayerID();
		switchPlayers();
		
		currentTurn++;
		gameStateID = gameStateID+1;
		
		return new GameState(Optional.of(fullMap), hidePlayerID(playerID.getUniquePlayerID()), gameStateID);
	}
	
	private PlayerState getPlayerState(String playerID) {
		PlayerState result = new PlayerState();
		for(PlayerState player: players) {
			if(player.getUniquePlayerID().equals(playerID)) {
				result = player;
			}
		}
		return result;
	}
	
	public void setEndGame(String playerID) {
		List<PlayerState> tempPlayers = new ArrayList<>();
		for(PlayerState player: players) {
			if(playerID.equals(player.getUniquePlayerID())) {
				tempPlayers.add(new PlayerState(player.getFirstName(),
												player.getLastName(),
												player.getUAccount(),
												EPlayerGameState.Lost,
												new UniquePlayerIdentifier(player.getUniquePlayerID()),
												player.hasCollectedTreasure()));
			} else {
				tempPlayers.add(new PlayerState(player.getFirstName(),
												player.getLastName(),
												player.getUAccount(),
												EPlayerGameState.Won,
												new UniquePlayerIdentifier(player.getUniquePlayerID()),
												player.hasCollectedTreasure()));
			}
		}
		players = tempPlayers;
	}
	
	//change
	private void switchPlayers() {
		List<PlayerState> tempPlayers = new ArrayList<>();
		PlayerState currentPlayer = getPlayerState(currentPlayerID);
		tempPlayers.add(new PlayerState(currentPlayer.getFirstName(),
										currentPlayer.getLastName(),
										currentPlayer.getUAccount(),
										EPlayerGameState.MustAct,
										new UniquePlayerIdentifier(currentPlayer.getUniquePlayerID()),
										currentPlayer.hasCollectedTreasure()));
		System.out.println("PlayerID must act: " + currentPlayer.getUniquePlayerID());
		PlayerState anotherPlayer = new PlayerState();
		for(PlayerState player: players) {
			if(!player.getUniquePlayerID().equals(currentPlayer.getUniquePlayerID())) {
				anotherPlayer = player;
				//currentPlayerID = player.getUniquePlayerID();
			}
		}
		tempPlayers.add(new PlayerState(anotherPlayer.getFirstName(),
										anotherPlayer.getLastName(),
										anotherPlayer.getUAccount(),
										EPlayerGameState.MustWait,
										new UniquePlayerIdentifier(anotherPlayer.getUniquePlayerID()),
										anotherPlayer.hasCollectedTreasure()));
		System.out.println("PlayerID must wait: " + anotherPlayer.getUniquePlayerID());
		players = tempPlayers;
	}
	
	private List<PlayerState> hidePlayerID(String playerID) {
		List<PlayerState> tempPlayers = new ArrayList<>();
		for(PlayerState player: players) {
			if(player.getUniquePlayerID().equals(playerID)) {
				tempPlayers.add(player);
			} else {
				tempPlayers.add(new PlayerState(player.getFirstName(),
												player.getLastName(),
												player.getUAccount(),
												player.getState(),
												new UniquePlayerIdentifier(UUID.randomUUID().toString()),
												player.hasCollectedTreasure()));
			}
		}
		return tempPlayers;
	}
	
	private FullMap changeMapForPlayer() {
		Set<FullMapNode> newMapNodes = new HashSet<>();
		for(FullMapNode node: fullMap.getMapNodes()) {
			if(node.getPlayerPositionState().equals(EPlayerPositionState.MyPlayerPosition)) {
				newMapNodes.add(new FullMapNode(node.getTerrain(),
							EPlayerPositionState.EnemyPlayerPosition, 
							node.getTreasureState(), 
							node.getFortState(), 
							node.getX(), 
							node.getY()));
			} else if(node.getPlayerPositionState().equals(EPlayerPositionState.EnemyPlayerPosition)) {
				newMapNodes.add(new FullMapNode(node.getTerrain(),
							EPlayerPositionState.MyPlayerPosition, 
							node.getTreasureState(), 
							node.getFortState(), 
							node.getX(), 
							node.getY()));
			} else {
				newMapNodes.add(new FullMapNode(node.getTerrain(),
							node.getPlayerPositionState(), 
							node.getTreasureState(), 
							node.getFortState(), 
							node.getX(), 
							node.getY()));
			}
		}
		for(FullMapNode node: newMapNodes) {
			if(node.getFortState().equals(EFortState.MyFortPresent)) {
				newMapNodes.add(new FullMapNode(node.getTerrain(),
							node.getPlayerPositionState(), 
							node.getTreasureState(), 
							EFortState.NoOrUnknownFortState, 
							node.getX(), 
							node.getY()));
			} else if(node.getFortState().equals(EFortState.EnemyFortPresent)) {
				newMapNodes.add(new FullMapNode(node.getTerrain(),
							node.getPlayerPositionState(), 
							node.getTreasureState(), 
							EFortState.MyFortPresent, 
							node.getX(), 
							node.getY()));
			} else {
				newMapNodes.add(new FullMapNode(node.getTerrain(),
							node.getPlayerPositionState(), 
							node.getTreasureState(), 
							node.getFortState(), 
							node.getX(), 
							node.getY()));
			}
		}

		return new FullMap(newMapNodes);
	}
}
