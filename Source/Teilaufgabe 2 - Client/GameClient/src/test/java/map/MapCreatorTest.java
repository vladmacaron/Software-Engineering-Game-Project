package map;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import client.mapcreator.MapCreator;
import client.model.Coordinates;
import client.model.Map;
import client.model.MapObject;
import client.model.ObjectType;
import client.model.TerrainType;

class MapCreatorTest {
	
	@Test
	void mapCreator_checkIslands_test() {
		HashMap<Coordinates, MapObject> fields = new HashMap<>();
		
		List<Coordinates> coords = new ArrayList<>();
		
		for(int x=2, y=3; x<6; x++, y--) {
			coords.add(new Coordinates(x, y));
		}
		
		for(int y = 0; y<4; y++) {
			for(int x = 0; x<8; x++) {
				TerrainType terrain = TerrainType.GRASS;
				if(coords.contains(new Coordinates(x,y))) {
					fields.put(new Coordinates(x,y), new MapObject(TerrainType.WATER, new ArrayList<ObjectType>()));
				} else {
					fields.put(new Coordinates(x,y), new MapObject(terrain, new ArrayList<ObjectType>()));
				}
			}
		}
		Map testMap = new Map(fields);
		
		assertFalse(MapCreator.validateMap(testMap));
		
		fields = new HashMap<>();
		for(int y = 0; y<4; y++) {
			for(int x = 0; x<8; x++) {
				fields.put(new Coordinates(x,y), new MapObject(TerrainType.WATER, new ArrayList<ObjectType>()));
			}
		}
		testMap = new Map(fields);
		
		assertFalse(MapCreator.validateMap(testMap));
	}
	
	@Test
	void mapCreator_checkBorders_test() {
		HashMap<Coordinates, MapObject> fields = new HashMap<>();
		
		for(int y = 0; y<4; y++) {
			for(int x = 0; x<8; x++) {
				TerrainType terrain = TerrainType.GRASS;
				if(y==0) {
					fields.put(new Coordinates(x,y), new MapObject(TerrainType.WATER, new ArrayList<ObjectType>()));
				} else {
					fields.put(new Coordinates(x,y), new MapObject(terrain, new ArrayList<ObjectType>()));
				}
			}
		}
		Map testMap = new Map(fields);
		
		assertFalse(MapCreator.validateMap(testMap));
		
		fields = new HashMap<>();
		for(int y = 0; y<4; y++) {
			for(int x = 0; x<8; x++) {
				fields.put(new Coordinates(x,y), new MapObject(TerrainType.WATER, new ArrayList<ObjectType>()));
			}
		}
		testMap = new Map(fields);
		
		assertFalse(MapCreator.validateMap(testMap));
	}
	
	@RepeatedTest(value = 20)
	void mapCreator_createMap_shouldCreateMapsWihoutFailure() {
		Map testMap = new Map();
		
		do {
			testMap = MapCreator.createPlayerMap();
		} while(!MapCreator.validateMap(testMap));
		
		assertTrue(MapCreator.validateMap(testMap));
	}
}
