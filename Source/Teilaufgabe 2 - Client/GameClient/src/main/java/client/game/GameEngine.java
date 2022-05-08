package client.game;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.GameState;
import client.ai.MapBrain;
import client.ai.PathBrain;
import client.converter.Converter;
import client.mapcreator.MapCreator;
import client.model.Coordinates;
import client.model.Map;
import client.model.MovementType;
import client.model.ObjectType;
import client.model.TerrainType;
import client.network.Network;

public class GameEngine {
	Map gameMap;
	String[] args;
	String serverBaseUrl;
	String gameID;
	String playerID = "";
	Network network;
	GameState gameState;
	
	MapBrain mapBrain;
	Set<Coordinates> visitedFields = new HashSet<>();
	
	public GameEngine(Map gameMap, String[] args) {
		super();
		this.gameMap = gameMap;
		this.args = args;
		
		serverBaseUrl = "http://swe1.wst.univie.ac.at";
		gameID = "K8jDO";
		
		if(args.length==3) {
			serverBaseUrl = args[1];
			gameID = args[2];
		}
		
		this.network = new Network(gameID, serverBaseUrl);
	}
	
	public void start() {
		playerID = network.registerPlayer("Vladislav", "Mazurov", "vladislavm95");
		
		readyToSendHalfMap();
		readyToReceiveFullMap();
		
		mapBrain = new MapBrain(gameMap, visitedFields);
		
		while(!checkEndGame()) {
			playMove();
		}
		
		if(checkGameState().equals(EPlayerGameState.Lost)) {
			System.out.println("You Lost");
		} else {
			System.out.println("You Win");
		}
		
	}
	
	private void waitTime() {
		try
		{
		    Thread.sleep(500);
		}
		catch(InterruptedException ex)
		{
		    Thread.currentThread().interrupt();
		}
	}
	
	private void readyToSendHalfMap()  {
		boolean canAct = false;
		gameState = network.getGameState().get();
		
		while(!canAct) {
			if(Converter.getPlayerState(gameState, playerID).equals(EPlayerGameState.MustAct)) {
				canAct = true;
			} else {
				waitTime();
				gameState = network.getGameState().get();
			}
		}
		
		Map playerMap = new Map();
		
		do {
			playerMap = MapCreator.createPlayerMap();
		} while (!MapCreator.validateMap(playerMap));
		
		gameMap.setMap(playerMap);
		network.sendHalfMap(playerMap);
		waitTime();
	}
	
	private void readyToReceiveFullMap() {
		boolean getFullMap = false;
		gameState = network.getGameState().get();
		
		while(!getFullMap) {
			if(!gameState.getMap().get().equals(null) && gameState.getMap().get().getMapNodes().size()==64) {
				getFullMap = true;
				gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
			} else {
				waitTime();
				gameState = network.getGameState().get();
			}
		}
	}
	
	private EPlayerGameState checkGameState() {
		gameState = network.getGameState().get();
		return Converter.getPlayerState(gameState, playerID);
	}
	
	private void playMove() {
		//Set<Coordinates> visitedFields = new HashSet<>();
		gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
		visitedFields.add(gameMap.getPlayerPosition());
		//MapBrain mapBrain = new MapBrain(gameMap, visitedFields);
		
		while(!checkGameState().equals(EPlayerGameState.MustAct)) {
			waitTime();
			gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
		}
		
		MovementType nextMove;
		
		if(Converter.hasTreasure(gameState, playerID)) {
			Coordinates goalCoord = mapBrain.findEnemyCastle();
			nextMove = mapBrain.nextMove(goalCoord);
			network.sendPlayerMove(Converter.convertToPlayerMove(playerID, nextMove));
		} else {
			Coordinates goalCoord = mapBrain.findTreasure();
			nextMove = mapBrain.nextMove(goalCoord);
			network.sendPlayerMove(Converter.convertToPlayerMove(playerID, nextMove));
		}
		
		
		/*
		if(mapBrain.getNextPossibleMove().stream().anyMatch(m -> (gameMap.getMapObject(m).getObjectsOnField().contains(ObjectType.TREASURE)))) {
			for(Coordinates goal : mapBrain.getNextPossibleMove()) {
				if(gameMap.getMapObject(goal).getObjectsOnField().contains(ObjectType.TREASURE)) {
					nextMove = mapBrain.nextMove(goal);
					network.sendPlayerMove(Converter.convertToPlayerMove(playerID, nextMove));
				} 
			}
		} else {
			nextMove = mapBrain.nextMove(mapBrain.getNextPossibleMove().stream().findFirst().get());
			network.sendPlayerMove(Converter.convertToPlayerMove(playerID, nextMove));
		}*/
		
		
		//for(Coordinates nextCoord : mapBrain.getNextPossibleMove()) {
		//	nextMove = mapBrain.nextMove(nextCoord);
		//}
	
		//network.sendPlayerMove(Converter.convertToPlayerMove(playerID, nextMove));
		gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
	}
	
	private boolean checkEndGame() {
		return checkGameState().equals(EPlayerGameState.Lost) || checkGameState().equals(EPlayerGameState.Won);
	}

}
