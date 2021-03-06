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
	
	//half map is created with random fields(thanks to collection shuffle), but the number of water fields
	//and mountain fields are hardcoded
	public static Map createPlayerMap() {
		HashMap<Coordinates, MapObject> fields = new HashMap<>();
		
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
	
	//validating created half map
	public static boolean validateMap(Map playerMap) {
		return checkIsland(playerMap) && checkBorders(playerMap);
	}
	
	//checking for islands with combination of other smaller checks
	private static boolean checkIsland(Map playerMap) {
		HashMap<Coordinates, MapObject> fields = playerMap.getMapFields();
		
		if(checkCorners(playerMap, new Coordinates(0,0))) {
			return false;
		}
		if(checkCorners(playerMap, new Coordinates(7,0))) {
			return false;
		}
		if(checkCorners(playerMap, new Coordinates(0,3))) {
			return false;
		}
		if(checkCorners(playerMap, new Coordinates(7,3))) {
			return false;
		}
		if(checkDiagonal(playerMap)) {
			return false;
		}
		
		for(int y = 0; y<MAX_Y; y++) {
			for(int x = 0; x<MAX_X; x++) {
				int countWater = 0;
				Coordinates cornerCoord = new Coordinates(x,y);
				for(Coordinates neighbour : cornerCoord.getNeighbours(0, 0, 7, 3)) {
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
	
	//checking corners of the map for water fields
	//this check is specific for checking corner island and water fields on the borders
	private static boolean checkCorners(Map playerMap, Coordinates cornerCoord) {
		HashMap<Coordinates, MapObject> fields = playerMap.getMapFields();
		
		int countWater = 0;
		for(Coordinates neighbour : cornerCoord.getNeighbours(0, 0, 7, 3)) {
			if(fields.get(neighbour).getTerrainType().equals(TerrainType.WATER)) {
				countWater++;
			}
		}
		
		return countWater>0;
	}
	
	//checking borders of them for water fields to follow game rules
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
	
	//check for a specific sort of island, when we have 4 water fields in the middle of the map positioned diagonally
	private static boolean checkDiagonal(Map playerMap) {
		HashMap<Coordinates, MapObject> fields = playerMap.getMapFields();
		int countWater = 0;
		for(int x=2, y=3; x<6; x++, y--) {
			if(fields.get(new Coordinates(x,y)).getTerrainType().equals(TerrainType.WATER)) {
				countWater++;
			}
		}
		if(countWater==4) {
			return true;
		}
		countWater = 0;
		for(int x=2, y=0; x<6; x++, y++) {
			if(fields.get(new Coordinates(x,y)).getTerrainType().equals(TerrainType.WATER)) {
				countWater++;
			}
		}
		if(countWater==4) {
			return true;
		}
		return false;
	}
	
}
