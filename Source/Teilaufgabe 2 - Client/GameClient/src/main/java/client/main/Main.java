package client.main;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import MessagesBase.ResponseEnvelope;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.ERequestState;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.GameState;
import client.converter.Converter;
import client.game.GameEngine;
import client.mapcreator.MapCreator;
import client.model.Map;
import client.network.Network;
import client.view.GameView;
import reactor.core.publisher.Mono;

public class Main {

	public static void main(String[] args) {
		
		Map map = new Map(); 
		GameView commandLineView = new GameView(map); 
		GameEngine gameEngine = new GameEngine(map, args);
		
		gameEngine.start();
		
	}
}
