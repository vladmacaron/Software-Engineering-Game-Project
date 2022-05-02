package client.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.ETreasureState;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.FullMapNode;
import MessagesBase.MessagesFromServer.GameState;
import MessagesBase.MessagesFromServer.PlayerState;
import client.model.Coordinates;
import client.model.Map;
import client.model.MapObject;
import client.model.MovementType;
import client.model.ObjectType;
import client.model.TerrainType;

public class Converter {
	public static HalfMap convertToHalfMap(String playerID, Map playerMap) {
		List<HalfMapNode> halfMapNodes = new ArrayList<>();
		
		//Boolean check = true;
		for (HashMap.Entry<Coordinates, MapObject> field : playerMap.getMapFields().entrySet()) {
			ETerrain terrain = convertToETerrain(field.getValue().getTerrainType());
			
			if(field.getValue().getObjectsOnField().contains(ObjectType.CASTLE)) {
				halfMapNodes.add(new HalfMapNode(field.getKey().getX(), field.getKey().getY(), true, terrain));
			} else {
				halfMapNodes.add(new HalfMapNode(field.getKey().getX(), field.getKey().getY(), terrain));
			}
		}
		
		HalfMap halfMap = new HalfMap(playerID, halfMapNodes);
		return halfMap;
	}
	
	public static EPlayerGameState getPlayerState(GameState gameState, String playerID) {
		for(PlayerState playerState: gameState.getPlayers()) {
			if(playerState.getUniquePlayerID().equals(playerID)) {
				return playerState.getState();
			}
		}
		return EPlayerGameState.MustWait;
	}
	
	public static Map convertToMap(FullMap fullMap) {
		HashMap<Coordinates, MapObject> fields = new HashMap<Coordinates, MapObject>();
		
		Collection<FullMapNode> halfMapNodes = fullMap.getMapNodes();
		for(FullMapNode mapNode : halfMapNodes) {
			TerrainType terrain = convertToTerrainType(mapNode.getTerrain());
			List<ObjectType> objects = new ArrayList<>();
			
			switch(mapNode.getFortState()) {
			case EnemyFortPresent:
				objects.add(ObjectType.ENEMY_CASTLE);
				break;
			case MyFortPresent:
				objects.add(ObjectType.CASTLE);
				break;
			default:
				break;
			}
			
			if(mapNode.getTreasureState().equals(ETreasureState.MyTreasureIsPresent)) {
				objects.add(ObjectType.TREASURE);
			}
			
			switch(mapNode.getPlayerPositionState()) {
			case BothPlayerPosition:
				objects.add(ObjectType.PLAYER);
				objects.add(ObjectType.ENEMY);
				break;
			case EnemyPlayerPosition:
				objects.add(ObjectType.ENEMY);
				break;
			case MyPlayerPosition:
				objects.add(ObjectType.PLAYER);
				break;
			case NoPlayerPresent:
				break;
			default:
				break;
			}
			
			fields.put(new Coordinates(mapNode.getX(), mapNode.getY()), new MapObject(terrain, objects));
		}
		return new Map(fields);
	}
	
	public static ETerrain convertToETerrain(TerrainType terrainType) {
		
		switch(terrainType) {
		case GRASS:
			return ETerrain.Grass;
		case MOUNTAIN:
			return ETerrain.Mountain;
		case WATER:
			return ETerrain.Water;
		default:
			//add exception
			return ETerrain.Grass;
		}
	}
	
	public static TerrainType convertToTerrainType(ETerrain eTerrain) {
		switch(eTerrain) {
		case Grass:
			return TerrainType.GRASS;
		case Mountain:
			return TerrainType.MOUNTAIN;
		case Water:
			return TerrainType.WATER;
		default:
			//add exception
			return TerrainType.GRASS;
		}
	}
	
	public static PlayerMove convertToPlayerMove(String playerID, MovementType movementType) {
		
		switch(movementType) {
		case DOWN:
			return PlayerMove.of(playerID, EMove.Down);
		case LEFT:
			return PlayerMove.of(playerID, EMove.Left);
		case RIGHT:
			return PlayerMove.of(playerID, EMove.Right);
		case UP:
			return PlayerMove.of(playerID, EMove.Up);
		default:
			//add exception
			return new PlayerMove();
		}
		
	}
}
