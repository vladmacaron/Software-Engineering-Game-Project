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
		
		for (HashMap.Entry<Coordinates, MapObject> field : playerMap.getMapField().entrySet()) {
			ETerrain terrain = convertToETerrain(field.getValue().getTerrainType()); 
			halfMapNodes.add(new HalfMapNode(field.getKey().getX(), field.getKey().getY(), terrain));
		}
		
		HalfMap halfMap = new HalfMap(playerID, halfMapNodes);
		return halfMap;
	}
	
	public ETerrain convertToETerrain(TerrainType terrainType) {
		if(terrainType.equals(TerrainType.GRASS)) {
			return ETerrain.Grass;
		} else if(terrainType.equals(TerrainType.MOUNTAIN)) {
			return ETerrain.Mountain;
		} else {
			return ETerrain.Water;
		}
	}
	
	public PlayerMove convertToPlayerMove(String playerID, MovementType movementType) {
		if(movementType.equals(MovementType.UP)) {
			return PlayerMove.of(playerID, EMove.Up);
		} else if(movementType.equals(MovementType.DOWN)) {
			return PlayerMove.of(playerID, EMove.Down);
		} else if(movementType.equals(MovementType.LEFT)) {
			return PlayerMove.of(playerID, EMove.Left);
		} else if(movementType.equals(MovementType.RIGHT)) {
			return PlayerMove.of(playerID, EMove.Right);
		} else {
			return new PlayerMove();
		}
	}
}
