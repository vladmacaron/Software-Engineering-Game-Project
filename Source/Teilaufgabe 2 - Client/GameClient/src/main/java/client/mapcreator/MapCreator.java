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
	
	static final int MAX_Y = 4;
	static final int MAX_X = 8;
	
	public static Map createPlayerMap() {
		//final int MAX_Y = 4;
		//final int MAX_X = 8;
	
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
		int randX = rand.nextInt(MAX_X);
		int randY = rand.nextInt(MAX_Y);
		Coordinates castleCoord = new Coordinates(randX, randY);
		//System.out.println("X: " + castleCoord.getX() + " Y: " + castleCoord.getY());
		
		while(!fields.get(castleCoord).getTerrainType().equals(TerrainType.GRASS)) {
			randX = rand.nextInt(MAX_X);
			randY = rand.nextInt(MAX_Y);
			castleCoord = new Coordinates(randX, randY);
		}
		
		if(fields.get(castleCoord).getTerrainType().equals(TerrainType.GRASS)) {
			fields.get(castleCoord).addObjectOnField(ObjectType.CASTLE);
		} 
		
		return new Map(fields);
	}
	
	public static boolean validateMap(Map playerMap) {
		return checkIsland(playerMap) && checkBorders(playerMap);
	}
	
	private static boolean checkIsland(Map playerMap) {
		HashMap<Coordinates, MapObject> fields = playerMap.getMapFields();
		
		if(!checkCorners(playerMap, new Coordinates(0,0))) {
			return false;
		}
		if(!checkCorners(playerMap, new Coordinates(7,0))) {
			return false;
		}
		if(!checkCorners(playerMap, new Coordinates(0,3))) {
			return false;
		}
		if(!checkCorners(playerMap, new Coordinates(7,3))) {
			return false;
		}
		
		for(int y = 0; y<MAX_Y; y++) {
			for(int x = 0; x<MAX_X; x++) {
				int countWater = 0;
				Coordinates cornerCoord = new Coordinates(x,y);
				for(Coordinates neighbour : cornerCoord.getNeighbours()) {
					if(fields.get(neighbour).getTerrainType().equals(TerrainType.WATER)) {
						countWater++;
					}
				}
				if(countWater>2) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private static boolean checkCorners(Map playerMap, Coordinates cornerCoord) {
		HashMap<Coordinates, MapObject> fields = playerMap.getMapFields();
		
		int countWater = 0;
		for(Coordinates neighbour : cornerCoord.getNeighbours()) {
			if(fields.get(neighbour).getTerrainType().equals(TerrainType.WATER)) {
				countWater++;
			}
		}
		
		return !(countWater>0);
	}
	
	private static boolean checkBorders(Map playerMap) {
		HashMap<Coordinates, MapObject> fields = playerMap.getMapFields();
		int countLongSide = 0, countShortSide = 0;
		for(int x=0; x<MAX_X; x++) {
			if(fields.get(new Coordinates(x, 0)).getTerrainType().equals(TerrainType.WATER)) {
				countLongSide++;
			}
			if(fields.get(new Coordinates(x, 3)).getTerrainType().equals(TerrainType.WATER)) {
				countLongSide++;
			}
		}
		
		for(int y=0; y<MAX_Y; y++) {
			if(fields.get(new Coordinates(0, y)).getTerrainType().equals(TerrainType.WATER)) {
				countShortSide++;
			}
			if(fields.get(new Coordinates(7, y)).getTerrainType().equals(TerrainType.WATER)) {
				countShortSide++;
			}
		}
		
		return !(countLongSide>3 || countShortSide>1);
	}
	
}
