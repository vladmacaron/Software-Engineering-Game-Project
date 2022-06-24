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
import MessagesBase.MessagesFromClient.ERequestState;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.GameState;
import MessagesBase.MessagesFromServer.PlayerState;
import game.Game;
import game.GameRules;
import game.IDCheck;
import game.IDCreator;
import map.HalfMapRules;
import server.exceptions.FieldTypeException;
import server.exceptions.GameIdException;
import server.exceptions.GenericExampleException;
import server.exceptions.HalfMapCastleException;
import server.exceptions.IslandException;
import server.exceptions.NumberOfFieldsException;
import server.exceptions.NumberOfPlayersException;
import server.exceptions.PlayerIdException;
import server.exceptions.TurnBasedException;
import server.exceptions.UniquePlayerIdException;
import server.exceptions.WaterBordersException;

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
			GameRules.checkNumberOfPlayers(gamesList.get(gameID.getUniqueGameID()));
			PlayerState newPlayer = new PlayerState(playerRegistration.getStudentFirstName(),
					playerRegistration.getStudentLastName(),
					playerRegistration.getStudentUAccount(),
					EPlayerGameState.MustWait,
					newPlayerID,
					false);
			gamesList.get(gameID.getUniqueGameID()).addPlayer(newPlayer);
		} catch(GenericExampleException e) {
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		}
		
		return playerIDMessage;
	}
	
	// receive player HalfMap
	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<?> receiveHalfMap(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody HalfMap halfMap) {
		
		try {
			Game currentGame = gamesList.get(gameID.getUniqueGameID());
			IDCheck.checkGameID(gamesList.keySet(), gameID.getUniqueGameID());
			IDCheck.checkPlayerID(currentGame, halfMap.getUniquePlayerID());
			HalfMapRules.checkHalfMap(halfMap);
			GameRules.checkBothPlayersAreRegistered(currentGame);
			if(currentGame.getLastTurnTime()!=null) {
				GameRules.checkTimeRule(currentGame.getLastTurnTime());
			}
			
			currentGame.addHalfMap(halfMap);
		} catch(TurnBasedException e) {
			gamesList.get(gameID.getUniqueGameID()).setEndGame(halfMap.getUniquePlayerID());
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		} catch(FieldTypeException e) {
			gamesList.get(gameID.getUniqueGameID()).setEndGame(halfMap.getUniquePlayerID());
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		} catch(HalfMapCastleException e) {
			gamesList.get(gameID.getUniqueGameID()).setEndGame(halfMap.getUniquePlayerID());
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		} catch(NumberOfFieldsException e) {
			gamesList.get(gameID.getUniqueGameID()).setEndGame(halfMap.getUniquePlayerID());
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		} catch(WaterBordersException e) {
			gamesList.get(gameID.getUniqueGameID()).setEndGame(halfMap.getUniquePlayerID());
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		} catch(IslandException e) {
			gamesList.get(gameID.getUniqueGameID()).setEndGame(halfMap.getUniquePlayerID());
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		} catch(GenericExampleException e) {
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		}
		
		return new ResponseEnvelope<>();
	}

	//GameState
	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> sendGameState(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) {

		try {
			Game currentGame = gamesList.get(gameID.getUniqueGameID());
			IDCheck.checkGameID(gamesList.keySet(), gameID.getUniqueGameID());
			IDCheck.checkPlayerID(currentGame, playerID.getUniquePlayerID());
			GameRules.checkNumberOfTurns(currentGame.getCurrentTurn());
			if(currentGame.getLastTurnTime()!=null) {
				GameRules.checkTimeRule(currentGame.getLastTurnTime());
			}
			
			GameState gameState = currentGame.getGameState(playerID);
			ResponseEnvelope<GameState> result = new ResponseEnvelope<GameState>(gameState); 

			return result;
		} catch(GenericExampleException e) {
			throw new GenericExampleException(e.getErrorName(), e.getLocalizedMessage());
		}
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
