package client.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import client.converter.Converter;
import client.model.Coordinates;
import client.model.Map;
import client.model.MovementType;
import client.model.ObjectType;
import client.model.TerrainType;

public class MapBrain {
	Map gameMap;
	Set<Coordinates> visitedFields;
	Stack<MovementType> path;
	HashMap<Coordinates, Integer> valueMapTreasure;
	HashMap<Coordinates, Integer> valueMapCastle;
	
	public MapBrain(Map gameMap, Set<Coordinates> visitedFields) {
		super();
		this.gameMap = gameMap;
		this.visitedFields = visitedFields;
		
		valueMapTreasure = new HashMap<>();
		valueMapCastle = new HashMap<>();
		
		path = new Stack<>();
	}

	public Set<Coordinates> getVisitedFields() {
		return visitedFields;
	}
	
	public void addVisitedPoint(Coordinates coord) {
		visitedFields.add(coord);
		valueMapTreasure.computeIfPresent(coord, (key, value) -> value + 1);
	}
	
	public MovementType getPossibleMove(HashMap<Coordinates, Integer> valueMap, int minX, int minY, int maxX, int maxY) {
		Coordinates currentCoord = gameMap.getPlayerPosition();
		int min = 999;
		Coordinates minCoord = null;
		
		if(gameMap.getMapObject(currentCoord).getTerrainType().equals(TerrainType.MOUNTAIN)) {
			for(Coordinates neighbour: currentCoord.getNeighbours(minX, minY, maxX, maxY)) {
				if(gameMap.getMapObject(neighbour).getObjectsOnField().contains(ObjectType.TREASURE) 
						|| gameMap.getMapObject(neighbour).getObjectsOnField().contains(ObjectType.ENEMY_CASTLE)) {
					valueMap.computeIfPresent(neighbour, (key, value) -> value = 0);
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
									path.push(nextMove(upCoord));
									return nextMove(upCoord);
								}
								if(!gameMap.getMapObject(leftCoord).getTerrainType().equals(TerrainType.WATER)) {
									path.push(nextMove(leftCoord));
									return nextMove(leftCoord);
								}
							} else if(currentX+1==neighbourX && currentY-1==neighbourY) {
								if(!gameMap.getMapObject(upCoord).getTerrainType().equals(TerrainType.WATER)) {
									path.push(nextMove(upCoord));
									return nextMove(upCoord);
								}
								if(!gameMap.getMapObject(rightCoord).getTerrainType().equals(TerrainType.WATER)) {
									path.push(nextMove(rightCoord));
									return nextMove(rightCoord);
								}
							} else if(currentX-1==neighbourX && currentY+1==neighbourY) {
								if(!gameMap.getMapObject(leftCoord).getTerrainType().equals(TerrainType.WATER)) {
									path.push(nextMove(leftCoord));
									return nextMove(leftCoord);
								}
								if(!gameMap.getMapObject(downCoord).getTerrainType().equals(TerrainType.WATER)) {
									path.push(nextMove(downCoord));
									return nextMove(downCoord);
								}
							} else if(currentX+1==neighbourX && currentY+1==neighbourY) {
								if(!gameMap.getMapObject(rightCoord).getTerrainType().equals(TerrainType.WATER)) {
									path.push(nextMove(rightCoord));
									return nextMove(rightCoord);
								}
								if(!gameMap.getMapObject(downCoord).getTerrainType().equals(TerrainType.WATER)) {
									path.push(nextMove(downCoord));
									return nextMove(downCoord);
								}
							}
						}
					
				} else {
					valueMap.computeIfPresent(neighbour, (key, value) -> value + 1);
				}
				
				if(gameMap.getMapObject(neighbour).getTerrainType().equals(TerrainType.MOUNTAIN)) {
					valueMap.computeIfPresent(neighbour, (key, value) -> value + 1);
				}
				/*if(visitedFields.contains(neighbour)) {
					valueMap.computeIfPresent(neighbour, (key, value) -> value + 2);
				}*/
			}
		}
		
		for(Coordinates neighbour: currentCoord.getNeighbours(minX, minY, maxX, maxY)) {
			//if(canMove(currentCoord, neighbour)) {
			//	System.out.println(valueMap.get(neighbour));
			//}
			if(canMove(currentCoord, neighbour) && gameMap.getMapObject(neighbour).getObjectsOnField().contains(ObjectType.TREASURE)) {
				path.push(nextMove(neighbour));
				return nextMove(neighbour);
			}
			if(canMove(currentCoord, neighbour) && gameMap.getMapObject(neighbour).getObjectsOnField().contains(ObjectType.ENEMY_CASTLE)) {
				path.push(nextMove(neighbour));
				return nextMove(neighbour);
			}
			if(canMove(currentCoord, neighbour) && valueMap.get(neighbour)<min && !gameMap.getMapObject(neighbour).getTerrainType().equals(TerrainType.WATER)) {
				min = valueMap.get(neighbour);
				minCoord = neighbour;
			}
		}
		
		if(valueMap.get(minCoord) < valueMap.get(currentCoord)) {
			path.push(nextMove(minCoord));
			return nextMove(minCoord);
		} else {
			valueMap.computeIfPresent(currentCoord, (key, value) -> value + 5);
			return nextMove(minCoord);
		}
		/*
		valueMap.computeIfPresent(currentCoord, (key, value) -> value + 5);
		while(path.contains(nextMove(currentCoord))) {
			path.remove(nextMove(currentCoord));
		}
		
		MovementType prevCoord = path.pop();
		switch(prevCoord) {
		case DOWN:
			prevCoord = MovementType.UP;
			break;
		case LEFT:
			prevCoord = MovementType.RIGHT;
			break;
		case RIGHT:
			prevCoord = MovementType.LEFT;
			break;
		case UP:
			prevCoord = MovementType.DOWN;
			break;
		default:
			break;
		}
		System.out.println(prevCoord.toString());
		path.push(prevCoord);
		return prevCoord;*/
	}
	
	public MovementType findTreasure() {
		MovementType result = null;
		Coordinates currentCoord = gameMap.getPlayerPosition();
		//System.out.println("Max X: " + gameMap.getMaxColumn() + "Max Y: " + gameMap.getMaxRow());
		if(valueMapTreasure.isEmpty()) {
			for(Coordinates coord: gameMap.getMapFields().keySet()) {
				switch(gameMap.getMapObject(coord).getTerrainType()) {
				case GRASS:
					valueMapTreasure.put(coord, 0);
					break;
				case MOUNTAIN:
					valueMapTreasure.put(coord, 0);
					break;
				case WATER:
					valueMapTreasure.put(coord, 999);
					break;
				default:
					valueMapTreasure.put(coord, 100);
					break;
				}
			}
		}
		if(gameMap.getMaxColumn()==15) {
			if(currentCoord.getX()<8 && currentCoord.getY()<4) {
				result = getPossibleMove(valueMapTreasure,0, 0, 7, 3);
			} else {
				result = getPossibleMove(valueMapTreasure,8, 0, gameMap.getMaxColumn(), gameMap.getMaxRow());
			}
		} else {
			if(currentCoord.getX()<8 && currentCoord.getY()<4) {
				result = getPossibleMove(valueMapTreasure, 0, 0, 7, 3);
			} else {
				result = getPossibleMove(valueMapTreasure, 0, 4, gameMap.getMaxColumn(), gameMap.getMaxRow());
			}
		}
		
		return result;
	}
	
	public MovementType findCastle() {
		Coordinates currentCoord = gameMap.getPlayerPosition();
		
		if(valueMapCastle.isEmpty()) {
			for(Coordinates coord: gameMap.getMapFields().keySet()) {
				switch(gameMap.getMapObject(coord).getTerrainType()) {
				case GRASS:
					valueMapCastle.put(coord, 0);
					break;
				case MOUNTAIN:
					valueMapCastle.put(coord, 0);
					break;
				case WATER:
					valueMapCastle.put(coord, 999);
					break;
				default:
					valueMapCastle.put(coord, 100);
					break;
				}
			}

			if(gameMap.getMaxColumn()==15) {
				if(currentCoord.getX()<8 && currentCoord.getY()<4) {
					for(int x=0, newValue = 160; x<8; x++, newValue -= 10) {
						for(int y=0; y<4; y++) {
							final int tempValueGrass = newValue-5;
							final int tempValueNormal = newValue;
							if(gameMap.getMapObject(new Coordinates(x, y)).getTerrainType().equals(TerrainType.GRASS)) {
								valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueNormal);
							} else {
								valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueGrass);
							}
						}
					}
				} else {
					for(int x=8, newValue = 100; x<16; x++, newValue += 10) {
						for(int y=0; y<4; y++) {
							final int tempValueGrass = newValue-5;
							final int tempValueNormal = newValue;
							if(gameMap.getMapObject(new Coordinates(x, y)).getTerrainType().equals(TerrainType.GRASS)) {
								valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueNormal);
							} else {
								valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueGrass);
							}
						}
					}
				}
			} else {
				if(currentCoord.getX()<8 && currentCoord.getY()<4) {
					for(int y=0, newValue = 130; y<4; y++, newValue -= 10) {
						for(int x=0; x<8; x++) {
							final int tempValueGrass = newValue-5;
							final int tempValueNormal = newValue;
							if(gameMap.getMapObject(new Coordinates(x, y)).getTerrainType().equals(TerrainType.GRASS)) {
								valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueNormal);
							} else {
								valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueGrass);
							}
						}
					}
				} else {
					for(int y=4, newValue = 100; y<8; y++, newValue += 10) {
						for(int x=0; x<8; x++) {
							final int tempValueGrass = newValue-5;
							final int tempValueNormal = newValue;
							if(gameMap.getMapObject(new Coordinates(x, y)).getTerrainType().equals(TerrainType.GRASS)) {
								valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueNormal);
							} else {
								valueMapCastle.computeIfPresent(new Coordinates(x, y), (key, value) -> value = tempValueGrass);
							}
						}
					}
				}
			}
		}
		
		return getPossibleMove(valueMapCastle, 0, 0, gameMap.getMaxColumn(), gameMap.getMaxRow());
	}
	
	public boolean isOnTheOtherSide() {
		Coordinates initialCoord = null;
		Coordinates currentCoord = gameMap.getPlayerPosition();
		for(Coordinates coord: gameMap.getMapFields().keySet()) {
			if(gameMap.getMapObject(coord).getObjectsOnField().contains(ObjectType.CASTLE)) {
				initialCoord = coord;
			}
		}
		if(gameMap.getMaxColumn()==15) {
			if(initialCoord.getX()<8 && initialCoord.getY()<4) {
				if(currentCoord.getX()<8 && currentCoord.getY()<4) {
					return false;
				} else {
					return true;
				}
			} else {
				if(currentCoord.getX()>=8 && currentCoord.getY()>=4) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			if(currentCoord.getX()<8 && currentCoord.getY()<4) {
				if(currentCoord.getX()<8 && currentCoord.getY()<4) {
					return false;
				} else {
					return true;
				}
			} else {
				if(currentCoord.getX()>=8 && currentCoord.getY()>=4) {
					return true;
				} else {
					return false;
				}
			}
		}
	}
	
	public MovementType nextMove(Coordinates goal) {
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
		
		return move;
	}
	
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
