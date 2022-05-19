package converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromServer.EFortState;
import MessagesBase.MessagesFromServer.EPlayerPositionState;
import MessagesBase.MessagesFromServer.ETreasureState;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.FullMapNode;
import client.converter.Converter;
import client.mapcreator.MapCreator;
import client.model.Coordinates;
import client.model.Map;
import client.model.MapObject;
import client.model.ObjectType;
import client.model.TerrainType;

class ConverterTest {

	@Test
	void converter_convertToHalfMap() {
		List<HalfMapNode> halfMapNodes = new ArrayList<>();
		HashMap<Coordinates, MapObject> fields = new HashMap<Coordinates, MapObject>();
		
		for (int x=0; x<8; x++) {
			for(int y=0; y<4; y++) {
				ETerrain terrainHalfMap = ETerrain.Grass;
				TerrainType terrainMap = TerrainType.GRASS;
				fields.put(new Coordinates(x, y), new MapObject(terrainMap, new ArrayList<>()));
				halfMapNodes.add(new HalfMapNode(x, y, terrainHalfMap));
			}
		}
		
		Map testMap = new Map(fields);
		String playerID = "dummyID";
		HalfMap halfMap = new HalfMap(playerID, halfMapNodes);
		
		HalfMap testHalfMap = Converter.convertToHalfMap("dummyID", testMap);
		
		assertEquals(halfMap, testHalfMap);
	}
	
	@Test
	void converter_convertToMap() {
		Set<FullMapNode> fullMapNodes = new HashSet<>();
		HashMap<Coordinates, MapObject> fields = new HashMap<Coordinates, MapObject>();
		
		for (int x=0; x<8; x++) {
			for(int y=0; y<8; y++) {
				ETerrain terrainFullMap = ETerrain.Grass;
				TerrainType terrainMap = TerrainType.GRASS;
				fields.put(new Coordinates(x, y), new MapObject(terrainMap, new ArrayList<>()));
				fullMapNodes.add(new FullMapNode(ETerrain.Grass,EPlayerPositionState.NoPlayerPresent, 
						ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
			}
		}
		
		
		Map testMap = new Map(fields);
		FullMap fullMap = new FullMap(fullMapNodes);
		
		Map testFullMap = Converter.convertToMap(fullMap);
		
		assertEquals(testMap, testFullMap);
	}

}
