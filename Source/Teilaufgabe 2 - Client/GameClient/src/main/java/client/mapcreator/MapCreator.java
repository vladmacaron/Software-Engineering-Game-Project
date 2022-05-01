package client.mapcreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import client.model.Coordinates;
import client.model.Map;
import client.model.MapObject;
import client.model.ObjectType;
import client.model.TerrainType;

public class MapCreator {
	
	public static Map createPlayerMap() {
		final int MAX_Y = 4;
		final int MAX_X = 8;
	
		HashMap<Coordinates, MapObject> fields = new HashMap();
		
		int numberOfWaterFields = 5;
		int numberOfMountainFields = 8;
		
		List<TerrainType> randomTerrain = new ArrayList<TerrainType>();
		for(int i=0; i < 32; i++) {
			if(numberOfWaterFields>0) {
				randomTerrain.add(TerrainType.WATER);
				numberOfWaterFields--;
			} else if(numberOfMountainFields>0) {
				randomTerrain.add(TerrainType.MOUNTAIN);
				numberOfMountainFields--;
			} else {
				randomTerrain.add(TerrainType.GRASS);
			}
		}
		Collections.shuffle(randomTerrain);
		
		for(int y = 0; y<MAX_Y; y++) {
			for(int x = 0; x<MAX_X; x++) {
				TerrainType terrain = randomTerrain.remove(0);
				fields.put(new Coordinates(x,y), new MapObject(terrain, new ArrayList<ObjectType>()));
			}
		}
		
		Random rand = new Random();
		Coordinates castleCoord = new Coordinates(rand.nextInt(MAX_X), rand.nextInt(MAX_Y));
		System.out.println("X: " + castleCoord.getX() + " Y: " + castleCoord.getY());
		
		while(!fields.get(castleCoord).getTerrainType().equals(TerrainType.GRASS)) {
			castleCoord = new Coordinates(rand.nextInt(MAX_X+1), rand.nextInt(MAX_Y+1));
		}
		
		if(fields.get(castleCoord).getTerrainType().equals(TerrainType.GRASS)) {
			fields.get(castleCoord).addObjectOnField(ObjectType.CASTLE);
		} 
		
		return new Map(fields);
	}
	
	public static boolean validateMap() {
		return checkIsland() && checkBorders();
	}
	
	private static boolean checkIsland() {
		
	}
	
	private static boolean checkBorders() {
		
	}
	
}
