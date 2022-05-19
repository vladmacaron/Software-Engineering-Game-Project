package map;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import client.model.Map;
import client.model.MapObject;
import client.model.ObjectType;
import client.model.TerrainType;

class MapObjectTest {

	@Test
	void mapObject_addObjects() {
		List<ObjectType> objectsList = new ArrayList<>();
		
		TerrainType defaultTerrain = TerrainType.GRASS;
		
		objectsList.add(ObjectType.CASTLE);
		objectsList.add(ObjectType.ENEMY);
		
		MapObject mapObjectTest = new MapObject(defaultTerrain, objectsList);
		
		assertThrows(RuntimeException.class,
	            ()->{	     
	            mapObjectTest.addObjectOnField(ObjectType.CASTLE);
	            });
	}
}
