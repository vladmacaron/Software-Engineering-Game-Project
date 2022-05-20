package converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
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
import client.model.MovementType;
import client.model.ObjectType;
import client.model.TerrainType;

class ConverterTest {

	@ParameterizedTest
	@CsvSource({"Grass, GRASS", "Mountain, MOUNTAIN", "Water, WATER"})
	void converter_convertToHalfMap(ETerrain eTerrain, TerrainType terrainType) {
		List<HalfMapNode> halfMapNodes = new ArrayList<>();
		HashMap<Coordinates, MapObject> fields = new HashMap<Coordinates, MapObject>();
		
		for (int x=0; x<8; x++) {
			for(int y=0; y<4; y++) {
				fields.put(new Coordinates(x, y), new MapObject(terrainType, new ArrayList<>()));
				halfMapNodes.add(new HalfMapNode(x, y, eTerrain));
			}
		}
		
		Map testMap = new Map(fields);
		String playerID = "dummyID";
		HalfMap halfMap = new HalfMap(playerID, halfMapNodes);
		
		HalfMap testHalfMap = Converter.convertToHalfMap("dummyID", testMap);
		
		assertEquals(halfMap, testHalfMap);
	}
	
	@ParameterizedTest
	@CsvSource({"Grass, GRASS", "Mountain, MOUNTAIN", "Water, WATER"})
	void converter_convertToMap(ETerrain eTerrain, TerrainType terrainType) {
		Set<FullMapNode> fullMapNodes = new HashSet<>();
		HashMap<Coordinates, MapObject> fields = new HashMap<Coordinates, MapObject>();
		
		for (int x=0; x<8; x++) {
			for(int y=0; y<8; y++) {
				fields.put(new Coordinates(x, y), new MapObject(terrainType, new ArrayList<>()));
				fullMapNodes.add(new FullMapNode(eTerrain,EPlayerPositionState.NoPlayerPresent, 
						ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, x, y));
			}
		}
		
		
		Map testMap = new Map(fields);
		FullMap fullMap = new FullMap(fullMapNodes);
		
		Map testFullMap = Converter.convertToMap(fullMap);
		
		assertEquals(testMap, testFullMap);
	}
	
	@Test
	void converter_convertMove() {
		String playerID = "dummyID";
		assertEquals(Converter.convertToPlayerMove(playerID, MovementType.DOWN), PlayerMove.of(playerID, EMove.Down));
		assertEquals(Converter.convertToPlayerMove(playerID, MovementType.UP), PlayerMove.of(playerID, EMove.Up));
		assertEquals(Converter.convertToPlayerMove(playerID, MovementType.LEFT), PlayerMove.of(playerID, EMove.Left));
		assertEquals(Converter.convertToPlayerMove(playerID, MovementType.RIGHT), PlayerMove.of(playerID, EMove.Right));
	}

}
