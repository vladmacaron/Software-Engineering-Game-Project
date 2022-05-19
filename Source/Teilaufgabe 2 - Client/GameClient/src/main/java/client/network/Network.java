package client.network;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private Logger logger;
	
	public Network(String gameID, String baseURL) {
		super();
		this.gameID = gameID;
		this.baseURL = baseURL;
		this.logger = LoggerFactory.getLogger(Network.class);
		
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
		if (resultReg.getState() == ERequestState.Error) {
			logger.error("Client error, errormessage: " + resultReg.getExceptionMessage());
			return "";
		} else {
			UniquePlayerIdentifier uniqueID = resultReg.getData().get();
			playerID = uniqueID.getUniquePlayerID();
			logger.info("My Player ID: " + uniqueID.getUniquePlayerID());
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
			logger.error("Client error sending Map, errormessage: " + resultReg.getExceptionMessage());
		} else {
			logger.info("Client has sent HalfMap successfully");
		}
	}
	
	public Optional<GameState> getGameState() {
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
				.uri("/" + gameID + "/states/" + playerID)
				.retrieve()
				.bodyToMono(ResponseEnvelope.class);
		
		ResponseEnvelope<GameState> resultReg = webAccess.block();
		
		if (resultReg.getState() == ERequestState.Error) {
			logger.error("Client error while getting GameState, errormessage: " + resultReg.getExceptionMessage());
			return Optional.empty();
		} else {
			Optional<GameState> state = resultReg.getData();
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
			logger.error("Client error while sending PlayerMove, errormessage: " + resultReg.getExceptionMessage());
		} else {
			logger.info("Client has sent PlayerMove successfully");
		}
	}
}
