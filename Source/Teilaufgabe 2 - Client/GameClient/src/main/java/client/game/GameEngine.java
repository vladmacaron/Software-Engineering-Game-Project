package client.game;

import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.GameState;
import client.converter.Converter;
import client.mapcreator.MapCreator;
import client.model.Map;
import client.network.Network;

public class GameEngine {
	Map gameMap;
	String serverBaseUrl;
	String gameID;
	String playerID = "";
	Network network;
	
	public GameEngine(Map gameMap, String serverBaseUrl, String gameID) {
		super();
		this.gameMap = gameMap;
		this.serverBaseUrl = serverBaseUrl;
		this.gameID = gameID;
		
		this.network = new Network(gameID, serverBaseUrl);
	}
	
	public void start() {
		boolean canAct = false;
		
		playerID = network.registerPlayer("Vladislav", "Mazurov", "vladislavm95");
		
		GameState gameState = network.getGameState().get();
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
		
		canAct = false;
		gameState = network.getGameState().get();
		while(!canAct) {
			if(Converter.getPlayerState(gameState, playerID).equals(EPlayerGameState.MustAct)) {
				gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
				canAct = true;
			} else {
				gameMap.setMap(Converter.convertToMap(gameState.getMap().get()));
				gameState = network.getGameState().get();
			}
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
	

}
