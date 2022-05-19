package map;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import client.exceptions.MapException;
import client.mapcreator.MapCreator;
import client.model.Coordinates;
import client.model.Map;
import client.model.MapObject;
import client.model.ObjectType;
import client.model.TerrainType;

class MapTest {

	@ParameterizedTest
	@CsvSource({"5,8,4","2,10,1","28,3,27","3,5,2"})
	void map_columns_test(int max_x, int max_y, int expected) {
		HashMap<Coordinates, MapObject> mapFields1 = new HashMap<Coordinates, MapObject>();
		
		for(int x=0; x<max_x; x++) {
			for(int y=0; y<max_y; y++) {
				mapFields1.put(new Coordinates(x, y), new MapObject(TerrainType.GRASS, new ArrayList<>()));
			}
		}
		
		Map mapTest1 = new Map(mapFields1);
		
		assertEquals(expected, mapTest1.getMaxColumn());
		
		assertThrows(RuntimeException.class,
	            ()->{	     
	            Map testMap = new Map();
	            testMap.getMaxColumn();
	            });
	} 
	
	@ParameterizedTest
	@CsvSource({"5,8,7","1,10,9","2,3,2","3,5,4"})
	void map_rows_test(int max_x, int max_y, int expected) {
		HashMap<Coordinates, MapObject> mapFields1 = new HashMap<Coordinates, MapObject>();
		
		for(int x=0; x<max_x; x++) {
			for(int y=0; y<max_y; y++) {
				mapFields1.put(new Coordinates(x, y), new MapObject(TerrainType.GRASS, new ArrayList<>()));
			}
		}
		
		Map mapTest1 = new Map(mapFields1);
		
		assertEquals(expected, mapTest1.getMaxRow());
		
		HashMap<Coordinates, MapObject> mapFields2 = new HashMap<Coordinates, MapObject>();
		
		assertThrows(RuntimeException.class,
	            ()->{	     
	            Map testMap = new Map();
	            testMap.getMaxRow();
	            });
	}
	
	@Test
	void map_setMap_test() {
		assertThrows(MapException.class,
	            ()->{
	            HashMap<Coordinates, MapObject> mapFields = null;
	            Map testMapInitial = new Map();
	            Map testMap = new Map(mapFields);
	            testMap.setMap(testMapInitial);
	            });
	}

}
