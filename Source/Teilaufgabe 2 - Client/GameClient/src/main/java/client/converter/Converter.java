package client.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import client.model.Coordinates;
import client.model.Map;
import client.model.MapObject;
import client.model.MovementType;
import client.model.TerrainType;

public class Converter {
	public HalfMap convertToHalfMap(String playerID, Map playerMap) {
		List<HalfMapNode> halfMapNodes = new ArrayList<>();
		
		for (HashMap.Entry<Coordinates, MapObject> field : playerMap.getMapFields().entrySet()) {
			ETerrain terrain = convertToETerrain(field.getValue().getTerrainType()); 
			halfMapNodes.add(new HalfMapNode(field.getKey().getX(), field.getKey().getY(), terrain));
		}
		
		HalfMap halfMap = new HalfMap(playerID, halfMapNodes);
		return halfMap;
	}
	
	public ETerrain convertToETerrain(TerrainType terrainType) {
		
		switch(terrainType) {
		case GRASS:
			return ETerrain.Grass;
		case MOUNTAIN:
			return ETerrain.Mountain;
		case WATER:
			return ETerrain.Mountain;
		default:
			//add exception
			return ETerrain.Grass;
		}
		
	}
	
	public PlayerMove convertToPlayerMove(String playerID, MovementType movementType) {
		
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
