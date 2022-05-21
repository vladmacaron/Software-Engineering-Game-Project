package client.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.converter.Converter;
import client.model.Coordinates;
import client.model.Map;
import client.model.MovementType;
import client.model.ObjectType;
import client.model.TerrainType;
import client.network.Network;

public class MapBrain {
	Map gameMap;
	HashMap<Coordinates, Integer> valueMapTreasure;
	HashMap<Coordinates, Integer> valueMapCastle;
	private Logger logger;
	
	//we have 2 different value maps for finding treasure and castle
	public MapBrain(Map gameMap) {
		super();
		this.gameMap = gameMap;
		this.logger = LoggerFactory.getLogger(MapBrain.class);
		
		valueMapTreasure = new HashMap<>();
		valueMapCastle = new HashMap<>();
	}
	
	//adding value to already visited coordinates
	public void addVisitedPoint(Coordinates coord) {
		valueMapTreasure.computeIfPresent(coord, (key, value) -> value + 1);
	}
	
	//getting next possible move of the player based on the neighbor fields values
	private MovementType getPossibleMove(HashMap<Coordinates, Integer> valueMap, int minX, int minY, int maxX, int maxY) {
		Coordinates currentCoord = gameMap.getPlayerPosition();
		int min = 999;
		Coordinates minCoord = null;
		
		//checking if current fields is mountain, in that case we see all neighbour fields
		if(gameMap.getMapObject(currentCoord).getTerrainType().equals(TerrainType.MOUNTAIN)) {
			for(Coordinates neighbour: currentCoord.getNeighbours(minX, minY, maxX, maxY)) {
				if(gameMap.getMapObject(neighbour).getTerrainType().equals(TerrainType.MOUNTAIN) || 
						gameMap.getMapObject(neighbour).getTerrainType().equals(TerrainType.GRASS)) {
					valueMap.computeIfPresent(neighbour, (key, value) -> value + 1);
				}
				
				if(gameMap.getMapObject(neighbour).getObjectsOnField().contains(ObjectType.TREASURE) 
						|| gameMap.getMapObject(neighbour).getObjectsOnField().contains(ObjectType.ENEMY_CASTLE)) {
					valueMap.computeIfPresent(neighbour, (key, value) -> value = 0);
					
					//this is an additional check for neighbour fields that are placed  diagonally and which we cant visit immediately
					//but if those fields have treasure/castle we will move closer to them
					if(!canMove(currentCoord, neighbour)) {
						int currentX = currentCoord.getX();
						int currentY = currentCoord.getY();
						Coordinates upCoord = new Coordinates(currentX, currentY-1);
						Coordinates leftCoord = new Coordinates(currentX-1, currentY);
						Coordinates downCoord = new Coordinates(currentX, currentY+1);
						Coordinates rightCoord = new Coordinates(currentX+1, currentY);
						
						int neighbourX = neighbour.getX();
						int neighbourY = neighbour.getY(); 
							
						if(currentX-1==neighbourX && currentY-1==neighbourY) {
							if(!gameMap.getMapObject(upCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(upCoord);
							}
							if(!gameMap.getMapObject(leftCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(leftCoord);
							}
						} else if(currentX+1==neighbourX && currentY-1==neighbourY) {
							if(!gameMap.getMapObject(upCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(upCoord);
							}
							if(!gameMap.getMapObject(rightCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(rightCoord);
							}
						} else if(currentX-1==neighbourX && currentY+1==neighbourY) {
							if(!gameMap.getMapObject(leftCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(leftCoord);
							}
							if(!gameMap.getMapObject(downCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(downCoord);
							}
						} else if(currentX+1==neighbourX && currentY+1==neighbourY) {
							if(!gameMap.getMapObject(rightCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(rightCoord);
							}
							if(!gameMap.getMapObject(downCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(downCoord);
							}
						}
					}
					
				}
			}
		}
		
		//getting field either if castle/treasure or with the min value
		for(Coordinates neighbour: currentCoord.getNeighbours(minX, minY, maxX, maxY)) {
			if(canMove(currentCoord, neighbour) && gameMap.getMapObject(neighbour).getObjectsOnField().contains(ObjectType.TREASURE)) {
				return nextMove(neighbour);
			}
			if(canMove(currentCoord, neighbour) && gameMap.getMapObject(neighbour).getObjectsOnField().contains(ObjectType.ENEMY_CASTLE)) {
				return nextMove(neighbour);
			}
			if(canMove(currentCoord, neighbour) && valueMap.get(neighbour)<min && !gameMap.getMapObject(neighbour).getTerrainType().equals(TerrainType.WATER)) {
				min = valueMap.get(neighbour);
				minCoord = neighbour;
			}
		}
		
		//if the current fields value is less than min, then we put this fields value higher
		//so that we will not go back to it
		if(valueMap.get(minCoord) < valueMap.get(currentCoord)) {
			return nextMove(minCoord);
		} else {
			valueMap.computeIfPresent(currentCoord, (key, value) -> value + 5);
			for(Coordinates neighbour: currentCoord.getNeighbours(minX, minY, maxX, maxY)) {
				if(valueMap.get(neighbour)<min) {
					if(!canMove(currentCoord, neighbour)) {
						int currentX = currentCoord.getX();
						int currentY = currentCoord.getY();
						Coordinates upCoord = new Coordinates(currentX, currentY-1);
						Coordinates leftCoord = new Coordinates(currentX-1, currentY);
						Coordinates downCoord = new Coordinates(currentX, currentY+1);
						Coordinates rightCoord = new Coordinates(currentX+1, currentY);
						
						int neighbourX = neighbour.getX();
						int neighbourY = neighbour.getY(); 
							
						if(currentX-1==neighbourX && currentY-1==neighbourY) {
							if(!gameMap.getMapObject(upCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(upCoord);
							}
							if(!gameMap.getMapObject(leftCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(leftCoord);
							}
						} else if(currentX+1==neighbourX && currentY-1==neighbourY) {
							if(!gameMap.getMapObject(upCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(upCoord);
							}
							if(!gameMap.getMapObject(rightCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(rightCoord);
							}
						} else if(currentX-1==neighbourX && currentY+1==neighbourY) {
							if(!gameMap.getMapObject(leftCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(leftCoord);
							}
							if(!gameMap.getMapObject(downCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(downCoord);
							}
						} else if(currentX+1==neighbourX && currentY+1==neighbourY) {
							if(!gameMap.getMapObject(rightCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(rightCoord);
							}
							if(!gameMap.getMapObject(downCoord).getTerrainType().equals(TerrainType.WATER)) {
								return nextMove(downCoord);
							}
						}
					}
				}
			}
			return nextMove(minCoord);
		}
	} 
	
	//before finding treasure we are populating value map with values, water fields are 999 so that we will not move to them
	//after that based on our current position, we will get neighbour fields only in the half map we are at
	//based on the rules of the game, only there we could find treasure
	public MovementType findTreasure() {
		Coordinates currentCoord = gameMap.getPlayerPosition();
		
		if(valueMapTreasure.isEmpty()) {
			for(Coordinates coord: gameMap.getMapFields().keySet()) {
				switch(gameMap.getMapObject(coord).getTerrainType()) {
				case GRASS:
					valueMapTreasure.put(coord, 0);
					break;
				case MOUNTAIN:
					valueMapTreasure.put(coord, 1);
					break;
				case WATER:
					valueMapTreasure.put(coord, 999);
					break;
				default:
					valueMapTreasure.put(coord, 100);
					break;
				}
			}
			logger.info("valueMapTreasure created");
		}
		if(gameMap.getMaxColumn()==15) {
			if(currentCoord.getX()<8 && currentCoord.getY()<4) {
				return getPossibleMove(valueMapTreasure,0, 0, 7, 3);
			} else {
				return getPossibleMove(valueMapTreasure,8, 0, gameMap.getMaxColumn(), gameMap.getMaxRow());
			}
		} else {
			if(currentCoord.getX()<8 && currentCoord.getY()<4) {
				return getPossibleMove(valueMapTreasure, 0, 0, 7, 3);
			} else {
				return getPossibleMove(valueMapTreasure, 0, 4, gameMap.getMaxColumn(), gameMap.getMaxRow());
			}
		}
	}
	
	//before we try to find enemy castle we populate value map for castle with values similar to trasure value map
	//then based on the current position of the player we populate this half of map with high values that are decreasing 
	//closer to the opposite half of the map, as based on the game rules castle could be only on the enemy side
	public MovementType findCastle() {
		Coordinates currentCoord = gameMap.getPlayerPosition();
		
		if(valueMapCastle.isEmpty()) {
			for(Coordinates coord: gameMap.getMapFields().keySet()) {
				switch(gameMap.getMapObject(coord).getTerrainType()) {
				case GRASS:
					valueMapCastle.put(coord, 0);
					break;
				case MOUNTAIN:
					valueMapCastle.put(coord, 1);
					break;
				case WATER:
					valueMapCastle.put(coord, 999);
					break;
				default:
					logger.warn("Received unknown type of terrain");
					valueMapCastle.put(coord, 100);
					break;
				}
			}

			if(gameMap.getMaxColumn()==15) {
				if(currentCoord.getX()<8 && currentCoord.getY()<4) {
					for(int x=0, newValue = 160; x<8; x++, newValue -= 10) {
						for(int y=0; y<4; y++) {
							fillValueMapCastle(newValue, x, y);
						}
					}
				} else {
					for(int x=8, newValue = 100; x<16; x++, newValue += 10) {
						for(int y=0; y<4; y++) {
							fillValueMapCastle(newValue, x, y);
						}
					}
				}
			} else {
				if(currentCoord.getX()<8 && currentCoord.getY()<4) {
					for(int y=0, newValue = 130; y<4; y++, newValue -= 10) {
						for(int x=0; x<8; x++) {
							fillValueMapCastle(newValue, x, y);
						}
					}
				} else {
					for(int y=4, newValue = 100; y<8; y++, newValue += 10) {
						for(int x=0; x<8; x++) {
							fillValueMapCastle(newValue, x, y);
						}
					}
				}
			}
			logger.info("valueMapCastle created");
		}
		
		return getPossibleMove(valueMapCastle, 0, 0, gameMap.getMaxColumn(), gameMap.getMaxRow());
	}
	
	//helper function to fill value map for finding castle
	private void fillValueMapCastle(int newValue, int x, int y) {
		final int tempValueGrass = newValue-5;
		final int tempValueNormal = newValue;
		if(gameMap.getMapObject(new Coordinates(x, y)).getTerrainType().equals(TerrainType.GRASS)) {
			valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueNormal);
		} else {
			valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueGrass);
		}
	}
	
	//based on the goal coordinates we are getting type of move
	private MovementType nextMove(Coordinates goal) {
		Coordinates currentCoord = gameMap.getPlayerPosition();
		MovementType move = null;
		int currentX = currentCoord.getX();
		int currentY = currentCoord.getY();
		int goalX = goal.getX();
		int goalY = goal.getY();
		
		if((currentX-1)==goalX && currentY==goalY) {
			move = MovementType.LEFT;
		} else if((currentX+1)==goalX && currentY==goalY) {
			move = MovementType.RIGHT;
		} else if(currentX==goalX && (currentY-1)==goalY) {
			move = MovementType.UP;
		} else if(currentX==goalX && (currentY+1)==goalY) {
			move = MovementType.DOWN;
		}
		
		if(move == null) {
			throw new NullPointerException("Move is null while getting MOvementTyoe out of goal Coordinate");
		}
		
		return move;
	}
	
	//checking if we can go to the neighbour coordinates directly with existing type of move
	private boolean canMove(Coordinates currentCoord, Coordinates neighbourCoord) {
		boolean check = false;
		int currentX = currentCoord.getX();
		int currentY = currentCoord.getY();
		int neighbourX = neighbourCoord.getX();
		int neighbourY = neighbourCoord.getY();
		
		if((currentX-1)==neighbourX && currentY==neighbourY) {
			check = true;
		} else if((currentX+1)==neighbourX && currentY==neighbourY) {
			check = true;
		} else if(currentX==neighbourX && (currentY-1)==neighbourY) {
			check = true;
		} else if(currentX==neighbourX && (currentY+1)==neighbourY) {
			check = true;
		}
		
		return check;
	}
}
