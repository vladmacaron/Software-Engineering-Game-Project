package client.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.GameState;
import client.ai.MapBrain;
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
	int roundNumber = 0;
	
	MapBrain mapBrain;
	Set<Coordinates> visitedFields = new HashSet<>();
	//List<Coordinates> visitedFields = new ArrayList<>();
	
	public GameEngine(Map gameMap, String[] args) {
		super();
		this.gameMap = gameMap;
		this.args = args;
		
		this.serverBaseUrl = "http://swe1.wst.univie.ac.at";
		this.gameID = "P8M0f";
		
		if(args.length==3) {
			this.serverBaseUrl = args[1];
			this.gameID = args[2];
		}
		
		this.network = new Network(gameID, serverBaseUrl);
	}
	
	public void start() {
		playerID = network.registerPlayer("Vladislav", "Mazurov", "vladislavm95");
		
		readyToSendHalfMap();
		readyToReceiveFullMap();
		
		mapBrain = new MapBrain(gameMap, visitedFields);
		
		while(!checkEndGame()) {
			if(Converter.getPlayerState(gameState, playerID).equals(EPlayerGameState.MustAct)) {
				playMove();
			}
			//playMove();
			//System.out.println("check");
		}
		
		if(Converter.getPlayerState(gameState, playerID).equals(EPlayerGameState.Lost)) {
			gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
			System.out.println("You Lost");
		} else {
			gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
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
		//gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
		return Converter.getPlayerState(gameState, playerID);
	}
	
	private void playMove() {
		gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
		//visitedFields.add(gameMap.getPlayerPosition());
		mapBrain.addVisitedPoint(gameMap.getPlayerPosition());
		//MapBrain mapBrain = new MapBrain(gameMap, visitedFields);
		/*while(checkGameState().equals(EPlayerGameState.MustWait)) {
			waitTime();
			//gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
		}*/
		
		MovementType nextMove;
		roundNumber++;
		if(Converter.hasTreasure(gameState, playerID)) {
			System.out.println("Round number: " + roundNumber + ", Player has treasure\n");
			nextMove = mapBrain.findCastle();
		} else {
			System.out.println("Round number: " + roundNumber + ", Player does not have treasure\n");
			nextMove = mapBrain.findTreasure();
		}

		network.sendPlayerMove(Converter.convertToPlayerMove(playerID, nextMove));
		waitTime();
		//gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
	}
	
	//private boolean checkRules() {
	//}
	
	private boolean checkEndGame() {
		EPlayerGameState playerGameState = checkGameState();
		return playerGameState.equals(EPlayerGameState.Lost) || playerGameState.equals(EPlayerGameState.Won);
	}

}
