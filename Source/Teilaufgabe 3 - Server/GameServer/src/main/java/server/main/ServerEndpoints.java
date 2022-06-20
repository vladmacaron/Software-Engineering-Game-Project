package server.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import MessagesBase.ResponseEnvelope;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.PlayerState;
import game.Game;
import game.IDCheck;
import game.IDCreator;
import server.exceptions.GameIdException;
import server.exceptions.GenericExampleException;
import server.exceptions.NumberOfPlayersException;
import server.exceptions.PlayerIdException;
import server.exceptions.UniquePlayerIdException;

@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {
	
	private HashMap<String, Game> gamesList = new LinkedHashMap<String, Game>() {
		private static final long serialVersionUID = 1L;
		
		//removes the oldest games after size overpass 999
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Game> eldest) {
			return this.size() > 999;
		}
	};
	
	// new game
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {
		
		String gameID = IDCreator.createGameID();
		UniqueGameIdentifier gameIdentifier = new UniqueGameIdentifier(gameID);
		Game newGame = new Game(gameIdentifier);
		gamesList.put(gameID, newGame);
		return gameIdentifier;
	}

	// player registration
	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {
		UniquePlayerIdentifier newPlayerID = new UniquePlayerIdentifier(UUID.randomUUID().toString());
		ResponseEnvelope<UniquePlayerIdentifier> playerIDMessage = new ResponseEnvelope<>(newPlayerID);
		
		try {
			IDCheck.checkGameID(gamesList.keySet(), gameID.getUniqueGameID());
			PlayerState newPlayer = new PlayerState(playerRegistration.getStudentFirstName(),
					playerRegistration.getStudentLastName(),
					playerRegistration.getStudentUAccount(),
					EPlayerGameState.MustWait,
					newPlayerID,
					false);
			gamesList.get(gameID.getUniqueGameID()).addPlayer(newPlayer);
		} catch(GameIdException e) {
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		} catch(NumberOfPlayersException e) {
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		}
		
		return playerIDMessage;
	}
	
	// receive player HalfMap
	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<String> receiveHalfMap(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody HalfMap halfMap) {
		
		try {
			IDCheck.checkGameID(gamesList.keySet(), gameID.getUniqueGameID());
			IDCheck.checkPlayerID(gamesList.get(gameID.getUniqueGameID()), halfMap.getUniquePlayerID());
			
		} catch(GameIdException e) {
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		} catch(PlayerIdException e) {
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		} catch(UniquePlayerIdException e) {
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		}
		
		ResponseEnvelope<String> halfMapMessage = new ResponseEnvelope<String>("Succesfull");
		return halfMapMessage;
	}

	/*
	 * Note, this is only the most basic way of handling exceptions in spring (but
	 * sufficient for our task) it would, for example struggle if you use multiple
	 * controllers. Add the exception types to the @ExceptionHandler which your
	 * exception handling should support the superclass catches subclasses aspect
	 * of try/catch also applies here. Hence, we recommend to simply extend your own
	 * Exceptions from the GenericExampleException. For larger projects, one would
	 * most likely want to use the HandlerExceptionResolver; see here
	 * https://www.baeldung.com/exception-handling-for-rest-with-spring
	 * 
	 * Ask yourself: Why is handling the exceptions in a different method than the
	 * endpoint methods a good solution?
	 */
	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());

		// reply with 200 OK as defined in the network documentation
		// Side note: We only do this here for simplicity reasons. For future projects,
		// you should check out HTTP status codes and
		// what they can be used for. Note, the WebClient used during the Client implementation can react
		// to them using the .onStatus(...) method.
		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}
