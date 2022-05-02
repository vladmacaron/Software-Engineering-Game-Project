package client.network;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import MessagesBase.ResponseEnvelope;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.ERequestState;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.GameState;
import client.converter.Converter;
import client.model.Map;
import client.view.GameView;
import reactor.core.publisher.Mono;

public class Network {
	
	private WebClient baseWebClient;
	private String gameID;
	private String playerID;
	private String baseURL;
	
	public Network(String gameID, String baseURL) {
		super();
		this.gameID = gameID;
		this.baseURL = baseURL;
		
		baseWebClient = WebClient.builder().baseUrl(baseURL + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
	}
	
	public String registerPlayer(String name, String surname, String uspace) {
		PlayerRegistration registration = new PlayerRegistration(name, surname, uspace);
		
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST)
				.uri("/" + gameID + "/players")
				.body(BodyInserters.fromValue(registration))
				.retrieve()
				.bodyToMono(ResponseEnvelope.class);
		
		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();
		// always check for errors, and if some are reported, at least print them to the
		// console (logging should always be preferred!)
		// so that you become aware of them during debugging! The provided server gives
		// you constructive error messages.
		if (resultReg.getState() == ERequestState.Error) {
			// typically happens if you forgot to create a new game before the client
			// execution or
			// forgot to adapt the run configuration so that it supplies the id of the new
			// game to the client
			// open http://swe1.wst.univie.ac.at:18235/games in your browser to create a new
			// game and obtain its game id
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
			return "";
		} else {
			UniquePlayerIdentifier uniqueID = resultReg.getData().get();
			playerID = uniqueID.getUniquePlayerID();
			System.out.println("My Player ID: " + uniqueID.getUniquePlayerID());
			return playerID;
		}
	}
	
	public void sendHalfMap(Map playerMap) {
		HalfMap halfMap = Converter.convertToHalfMap(playerID, playerMap);
		
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST)
				.uri("/" + gameID + "/halfmaps")
				.body(BodyInserters.fromValue(halfMap))
				.retrieve()
				.bodyToMono(ResponseEnvelope.class);
		
		ResponseEnvelope resultReg = webAccess.block();
		
		if (resultReg.getState() == ERequestState.Error) {
			System.err.println("Client error sending Map, errormessage: " + resultReg.getExceptionMessage());
		} else {
			System.err.println("Client has sent HalfMap successfully");
		}
	}
	
	public Optional<GameState> getGameState() {
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
				.uri("/" + gameID + "/states/" + playerID)
				.retrieve()
				.bodyToMono(ResponseEnvelope.class);
		
		ResponseEnvelope<GameState> resultReg = webAccess.block();
		
		if (resultReg.getState() == ERequestState.Error) {
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
			return Optional.empty();
		} else {
			Optional<GameState> state = resultReg.getData();
			System.out.println("GameState: " + resultReg.getData().toString());
			return state;
		}
	}
	
	public String getPlayerID() {
		return playerID;
	}

	//TODO
	public void sendPlayerMove(PlayerMove move) {
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST)
				.uri("/" + gameID + "/moves")
				.body(BodyInserters.fromValue(move))
				.retrieve()
				.bodyToMono(ResponseEnvelope.class);
		
		ResponseEnvelope resultReg = webAccess.block();
		
		if (resultReg.getState() == ERequestState.Error) {
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
		} else {
			System.err.println("Client has sent PlayerMove successfully");
		}
	}
}
