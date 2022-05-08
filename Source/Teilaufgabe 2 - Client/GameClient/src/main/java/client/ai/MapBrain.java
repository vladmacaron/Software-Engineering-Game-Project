package client.ai;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import client.converter.Converter;
import client.model.Coordinates;
import client.model.Map;
import client.model.MovementType;
import client.model.ObjectType;
import client.model.TerrainType;

public class MapBrain {
	Map gameMap;
	Set<Coordinates> visitedFields;
	
	public MapBrain(Map gameMap, Set<Coordinates> visitedFields) {
		super();
		this.gameMap = gameMap;
		this.visitedFields = visitedFields;
	}

	public Set<Coordinates> getVisitedFields() {
		return visitedFields;
	}
	
	public void addVisitedPoint(Coordinates coord) {
		visitedFields.add(coord);
	}
	
	public Coordinates findTreasure() {
		Coordinates goal = null;
		for(Coordinates possibleField: getPossibleMoveToFindTreasure()) {
			if(gameMap.getMapObject(possibleField).getTerrainType().equals(TerrainType.GRASS)) {
				goal = possibleField;
			}
		}
		for(Coordinates possibleField: getPossibleMoveToFindTreasure()) {
			if(gameMap.getMapObject(possibleField).getTerrainType().equals(TerrainType.MOUNTAIN)) {
				goal = possibleField;
			}
		}
		for(Coordinates possibleField: getPossibleMoveToFindTreasure()) {
			if(gameMap.getMapObject(possibleField).getObjectsOnField().contains(ObjectType.TREASURE)) {
				goal = possibleField;
			}
		}
		return goal;
	}
	
	public Coordinates findEnemyCastle() {
		Coordinates goal = null;
		for(Coordinates possibleField: getPossibleMoveToFindEnemyCastle()) {
			if(gameMap.getMapObject(possibleField).getTerrainType().equals(TerrainType.GRASS)) {
				goal = possibleField;
			}
		}
		for(Coordinates possibleField: getPossibleMoveToFindEnemyCastle()) {
			if(gameMap.getMapObject(possibleField).getTerrainType().equals(TerrainType.MOUNTAIN)) {
				goal = possibleField;
			}
		}
		for(Coordinates possibleField: getPossibleMoveToFindEnemyCastle()) {
			if(gameMap.getMapObject(possibleField).getObjectsOnField().contains(ObjectType.ENEMY_CASTLE)) {
				goal = possibleField;
			}
		}
		return goal;
	}
	
	public Set<Coordinates> getPossibleMove(int minX, int minY, int maxX, int maxY) {
		Set<Coordinates> result = new HashSet<>();
		Coordinates currentCoord = gameMap.getPlayerPosition();
		for(Coordinates neighbour: currentCoord.getNeighbours(minX, minY, maxX, maxY)) {
			if(canMove(currentCoord, neighbour)) {
				if(!visitedFields.contains(neighbour) && !gameMap.getMapObject(neighbour).getTerrainType().equals(TerrainType.WATER)) {
					result.add(neighbour);
					//System.out.println(neighbour.getX() + " " + neighbour.getY());
				}
			}
		}
		if(result.isEmpty()) {
			for(Coordinates neighbour: currentCoord.getNeighbours(0, 0, gameMap.getMaxColumn(), gameMap.getMaxRow())) {
				if(canMove(currentCoord, neighbour)) {
					if(!gameMap.getMapObject(neighbour).getTerrainType().equals(TerrainType.WATER)) {
						result.add(neighbour);
						//System.out.println(neighbour.getX() + " " + neighbour.getY());
					}
				}
			}
		}
		return result;
	}
	
	public Set<Coordinates> getPossibleMoveToFindEnemyCastle() {
		Set<Coordinates> result = new HashSet<>();
		Coordinates currentCoord = gameMap.getPlayerPosition();
		System.out.println("Max X: " + gameMap.getMaxColumn() + "Max Y: " + gameMap.getMaxRow());
		
		if(gameMap.getMaxColumn()==15) {
			if(currentCoord.getX()<8 && currentCoord.getY()<4) {
				result = getPossibleMove(0, 0, gameMap.getMaxColumn(),gameMap.getMaxRow());
			} else {
				result = getPossibleMove(0, 0, gameMap.getMaxColumn(),gameMap.getMaxRow());
			}
		} else {
			if(currentCoord.getX()<8 && currentCoord.getY()<4) {
				result = getPossibleMove(0, 0, gameMap.getMaxColumn(),gameMap.getMaxRow());
			} else {
				result = getPossibleMove(0, 0, gameMap.getMaxColumn(),gameMap.getMaxRow());
			}
		}
		
		
		return result;
	}
	
	public Set<Coordinates> getPossibleMoveToFindTreasure() {
		Set<Coordinates> result = new HashSet<>();
		Coordinates currentCoord = gameMap.getPlayerPosition();
		//System.out.println("Max X: " + gameMap.getMaxColumn() + "Max Y: " + gameMap.getMaxRow());
		
		if(gameMap.getMaxColumn()==15) {
			if(currentCoord.getX()<8 && currentCoord.getY()<4) {
				result = getPossibleMove(0, 0, 7, 3);
			} else {
				result = getPossibleMove(8, 0, gameMap.getMaxColumn(), gameMap.getMaxRow());
			}
		} else {
			if(currentCoord.getX()<8 && currentCoord.getY()<4) {
				result = getPossibleMove(0, 0, 7, 3);
			} else {
				result = getPossibleMove(0, 4, gameMap.getMaxColumn(), gameMap.getMaxRow());
			}
		}
		
		return result;
	}
	
	public MovementType nextMove(Coordinates goal) {
		Coordinates currentCoord = gameMap.getPlayerPosition();
		MovementType move = null;
		int currentX = currentCoord.getX();
		int currentY = currentCoord.getY();
		int goalX = goal.getX();
		int goalY = goal.getY();
		
		//System.out.println("CurrentCoord: " + currentX + " " + currentY);
		//System.out.println("Goal: " + goalX + " " + goalY);
		
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
	
	public boolean canMove(Coordinates currentCoord, Coordinates neighbourCoord) {
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
