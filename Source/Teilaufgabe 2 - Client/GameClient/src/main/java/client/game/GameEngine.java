package client.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	Logger logger;
	
	MapBrain mapBrain;
	
	public GameEngine(Map gameMap, String[] args) {
		super();
		this.gameMap = gameMap;
		this.args = args;
		
		this.serverBaseUrl = "http://swe1.wst.univie.ac.at";
		this.gameID = "VEhDK";
		
		if(args.length==3) {
			this.serverBaseUrl = args[1];
			this.gameID = args[2];
		}
		
		this.logger = LoggerFactory.getLogger(GameEngine.class);
		
		this.network = new Network(gameID, serverBaseUrl);
	}
	
	public void start() {
		playerID = network.registerPlayer("Vladislav", "Mazurov", "vladislavm95");
		
		sendHalfMap();
		receiveFullMap();
		
		mapBrain = new MapBrain(gameMap);
		
		while(!checkEndGame()) {
			if(Converter.getPlayerState(gameState, playerID).equals(EPlayerGameState.MustAct)) {
				playMove();
			}
		}
		
		if(Converter.getPlayerState(gameState, playerID).equals(EPlayerGameState.Lost)) {
			setMap(Converter.convertToMap(gameState.getMap().get()));
			System.out.println("You Lost");
		} else {
			setMap(Converter.convertToMap(gameState.getMap().get()));
			System.out.println("You Win");
		}
		
	}
	
	//blocking main thread for half a second to follow game rules
	private void waitTime() {
		try {
		    Thread.sleep(500);
		} catch(InterruptedException ex) {
			logger.warn(ex.getMessage());
		    Thread.currentThread().interrupt();
		}
	}
	
	private void sendHalfMap()  {
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
			logger.info("Trying to create correct HalfMap");
		} while (!MapCreator.validateMap(playerMap));
		
		setMap(playerMap);
		network.sendHalfMap(playerMap);
		waitTime();
	}
	
	private void receiveFullMap() {
		boolean getFullMap = false;
		gameState = network.getGameState().get();
		
		while(!getFullMap) {
			if(!gameState.getMap().get().equals(null) && gameState.getMap().get().getMapNodes().size()==64) {
				getFullMap = true;
				setMap(Converter.convertToMap(gameState.getMap().get()));
				System.out.println("\n--------------Start----------------\n");
			} else {
				waitTime();
				gameState = network.getGameState().get();
			}
		}
	}
	
	private void setMap(Map map) {
		try {
			gameMap.setMap(map);
		} catch(Exception e) {
			logger.error(e.toString());
		}
	}
	
	private EPlayerGameState checkGameState() {
		gameState = network.getGameState().get();
		return Converter.getPlayerState(gameState, playerID);
	}
	
	private void playMove() {
		setMap(Converter.convertToMap(gameState.getMap().get()));
		mapBrain.addVisitedPoint(gameMap.getPlayerPosition());
		
		MovementType nextMove;
		roundNumber++;
		if(Converter.hasTreasure(gameState, playerID)) {
			System.out.println("Round number: " + roundNumber + ", You have treasure\n");
			nextMove = mapBrain.findCastle();
		} else {
			System.out.println("Round number: " + roundNumber + ", You do not have treasure\n");
			nextMove = mapBrain.findTreasure();
		}

		network.sendPlayerMove(Converter.convertToPlayerMove(playerID, nextMove));
		waitTime();
	}
	
	private boolean checkEndGame() {
		EPlayerGameState playerGameState = checkGameState();
		return playerGameState.equals(EPlayerGameState.Lost) || playerGameState.equals(EPlayerGameState.Won);
	}

}
