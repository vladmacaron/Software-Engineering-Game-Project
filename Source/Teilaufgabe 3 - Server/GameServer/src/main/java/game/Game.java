package game;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.EPlayerPositionState;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.FullMapNode;
import MessagesBase.MessagesFromServer.GameState;
import MessagesBase.MessagesFromServer.PlayerState;
import map.FullMapCreator;
import server.exceptions.NumberOfHalfMapsException;
import server.exceptions.UniquePlayerIdException;

public class Game {
	private UniqueGameIdentifier gameID;
	private String gameStateID;
	private List<PlayerState> players;
	private List<HalfMap> playersHalfMaps;
	private Map<String, FullMap> fullMap;
	//private Map<String, FullMapNode> treasureLocations;
	private int currentTurn;
	private LocalTime lastTurnTime;
	private String currentPlayerID;
	
	public Game(UniqueGameIdentifier gameID) {
		this.gameID = gameID;
		this.gameStateID = gameID.getUniqueGameID();
		this.players = new ArrayList<>();
		this.playersHalfMaps = new ArrayList<>();
		this.fullMap = new HashMap<>();
		this.currentTurn = 0;
		this.lastTurnTime = null;
		this.currentPlayerID = "";
	}
	
	public List<PlayerState> getPlayers() {
		return players;
	}
	
	public List<HalfMap> getPlayersHalfMaps() {
		return playersHalfMaps;
	}
	
	public LocalTime getLastTurnTime() {
		return lastTurnTime;
	}
	
	public int getCurrentTurn() {
		return currentTurn;
	}
	
	public String getCurrentPlayerID() {
		return currentPlayerID;
	}
 	
	public void addPlayer(PlayerState player) {
		if(players.contains(player)) {
			throw new UniquePlayerIdException("Unique PlayerID check", "This player is already registered for the game");
		}
		
		players.add(player);
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
		for(HalfMap checkHalfMap: playersHalfMaps) {
			if(checkHalfMap.getUniquePlayerID().equals(halfMap.getUniquePlayerID())) {
				setEndGame(halfMap.getUniquePlayerID());
				throw new NumberOfHalfMapsException("Number of HalfMaps check", "Player cannot send HalfMap more than 1 time");
			}
		}
		
		playersHalfMaps.add(halfMap);
		
		changeCurrentPlayerID();
		switchPlayers();
		
		//currentTurn++;
		gameStateID = gameStateID+1;
		
		if(playersHalfMaps.size() == 2) {
			fullMap = FullMapCreator.createFullMap(playersHalfMaps);
			//add treasure location here
		}
		
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
			return new GameState(hidePlayerID(playerID.getUniquePlayerID()), gameStateID);
		}
		
		GameState gameState = new GameState();
		
		if(currentTurn<21) {
			Map<String, FullMap> tempMaps = getRandomEnemyPosition(playerID.getUniquePlayerID());
			gameState = new GameState(Optional.of(tempMaps.get(playerID.getUniquePlayerID())), hidePlayerID(playerID.getUniquePlayerID()), gameStateID);
		} else {
			gameState = new GameState(Optional.of(fullMap.get(playerID.getUniquePlayerID())), hidePlayerID(playerID.getUniquePlayerID()), gameStateID);
		}
		
		//changeCurrentPlayerID();
		//switchPlayers();
		
		//currentTurn++;
		//gameStateID = gameStateID+1;
		
		return gameState;
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
	
	private void switchPlayers() {
		List<PlayerState> tempPlayers = new ArrayList<>();
		PlayerState currentPlayer = getPlayerState(currentPlayerID);
		tempPlayers.add(new PlayerState(currentPlayer.getFirstName(),
										currentPlayer.getLastName(),
										currentPlayer.getUAccount(),
										EPlayerGameState.MustAct,
										new UniquePlayerIdentifier(currentPlayer.getUniquePlayerID()),
										currentPlayer.hasCollectedTreasure()));
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
	
	private Map<String, FullMap> getRandomEnemyPosition(String playerID) {
		Set<FullMapNode> mapNodeFirstPlayer = new HashSet<>();
		Set<FullMapNode> mapNodeSecondPlayer = new HashSet<>();
		
		int maxX = 0;
		int maxY = 0;
		for(FullMapNode node: fullMap.get(playerID).getMapNodes()) {
			if(node.getX()>maxX) {
				maxX = node.getX();
			}
			if(node.getY()>maxY) {
				maxY = node.getY();
			}
		}
		
		int randomX = new Random().nextInt(maxX+1);
		int randomY = new Random().nextInt(maxY+1);
		
		while(!getMapNode(randomX, randomY, fullMap.get(playerID)).getTerrain().equals(ETerrain.Grass)) {
			randomX = new Random().nextInt(maxX+1);
			randomY = new Random().nextInt(maxY+1);
		}
		
		for(FullMapNode node: fullMap.get(playerID).getMapNodes()) {
			if(node.getX() == randomX && node.getY() == randomY) {
				if(node.getPlayerPositionState().equals(EPlayerPositionState.MyPlayerPosition)) {
					mapNodeFirstPlayer.add(new FullMapNode(node.getTerrain(),
													EPlayerPositionState.BothPlayerPosition, 
													node.getTreasureState(), 
													node.getFortState(), 
													node.getX(), 
													node.getY()));
				} else {
					mapNodeFirstPlayer.add(new FullMapNode(node.getTerrain(),
													EPlayerPositionState.EnemyPlayerPosition, 
													node.getTreasureState(), 
													node.getFortState(), 
													node.getX(), 
													node.getY()));
				}
			} else {
				mapNodeFirstPlayer.add(new FullMapNode(node.getTerrain(),
												node.getPlayerPositionState(), 
												node.getTreasureState(), 
												node.getFortState(), 
												node.getX(), 
												node.getY()));
			}
		}
		
		String otherPlayerID = "";
		
		for(PlayerState player: players) {
			if(!player.getUniquePlayerID().equals(playerID)) {
				otherPlayerID = player.getUniquePlayerID();
				break;
			}
		}
		
		for(FullMapNode node: fullMap.get(otherPlayerID).getMapNodes()) {
			if(node.getX() == randomX && node.getY() == randomY) {
				if(node.getPlayerPositionState().equals(EPlayerPositionState.MyPlayerPosition)) {
					mapNodeSecondPlayer.add(new FullMapNode(node.getTerrain(),
													EPlayerPositionState.BothPlayerPosition, 
													node.getTreasureState(), 
													node.getFortState(), 
													node.getX(), 
													node.getY()));
				} else {
					mapNodeSecondPlayer.add(new FullMapNode(node.getTerrain(),
													EPlayerPositionState.EnemyPlayerPosition, 
													node.getTreasureState(), 
													node.getFortState(), 
													node.getX(), 
													node.getY()));
				}
			} else {
				mapNodeSecondPlayer.add(new FullMapNode(node.getTerrain(),
												node.getPlayerPositionState(), 
												node.getTreasureState(), 
												node.getFortState(), 
												node.getX(), 
												node.getY()));
			}
		}
		
		Map<String, FullMap> tempFullMaps = new HashMap<>();
		tempFullMaps.put(playerID, new FullMap(mapNodeFirstPlayer));
		tempFullMaps.put(otherPlayerID, new FullMap(mapNodeSecondPlayer));

		return tempFullMaps;
	}
	
	private FullMapNode getMapNode(int x, int y, FullMap fullMap) {
		for(FullMapNode node: fullMap.getMapNodes()) {
			if(node.getX()==x && node.getY()==y) {
				return node;
			}
		}
		return new FullMapNode();
	}
}
