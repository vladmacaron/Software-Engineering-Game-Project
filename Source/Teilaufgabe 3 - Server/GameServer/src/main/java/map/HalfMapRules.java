package map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import server.exceptions.FieldTypeException;
import server.exceptions.GenericExampleException;
import server.exceptions.HalfMapCastleException;
import server.exceptions.HalfMapException;
import server.exceptions.IslandException;
import server.exceptions.NumberOfFieldsException;
import server.exceptions.WaterBordersException;

public class HalfMapRules {
	public static void checkHalfMap(HalfMap halfMap) {
		checkFieldsType(halfMap);
		checkCastle(halfMap);
		checkNumberOfFields(halfMap);
		checkBorders(halfMap);
		checkIsland(halfMap);
	}
	
	private static void checkFieldsType(HalfMap halfMap) throws FieldTypeException {
		int countWaterFields = 0;
		int countMountainFields = 0;
		int countGrassFields = 0; 
		for(HalfMapNode node: halfMap.getMapNodes()) {
			switch(node.getTerrain()) {
			case Grass:
				countGrassFields++;
				break;
			case Mountain:
				countMountainFields++;
				break;
			case Water:
				countWaterFields++;
				break;
			default:
				throw new FieldTypeException("Field Type check", "Cant find this type of field");
			}
		}
		if(countWaterFields<4) {
			throw new FieldTypeException("Field Type check", "Number of water fields is less than 4");
		}
		if(countMountainFields<3) {
			throw new FieldTypeException("Field Type check", "Number of mountain fields is less than 3");
		}
		if(countGrassFields<15) {
			throw new FieldTypeException("Field Type check", "Number of grass fields is less than 15");
		}
	}
	
	private static void checkCastle(HalfMap halfMap) throws HalfMapCastleException {
		int countCastle = 0;
		for(HalfMapNode node: halfMap.getMapNodes()) {
			if(node.isFortPresent()) {
				countCastle++;
				if(!node.getTerrain().equals(ETerrain.Grass)) {
					throw new HalfMapCastleException("Half Map Castle check", "Castle could not be places on non grass field");
				}
			}
		}
		if(countCastle!=1) {
			throw new HalfMapCastleException("Half Map Castle check", "Cant find castle or there are more than 2 castled on HalfMap");
		}
	}
	
	private static void checkNumberOfFields(HalfMap halfMap) throws NumberOfFieldsException {
		Set<HalfMapNode> nodes = new HashSet<>(halfMap.getMapNodes());
		if(nodes.size()!=32) {
			throw new NumberOfFieldsException("Number of fields check", "Map does not contain 32 fields");
		}
		for(HalfMapNode node: nodes) {
			if(node.getX()<0 || node.getX()>7) {
				throw new NumberOfFieldsException("Number of fields check", "Map does not conform to 4x8 size");
			}
			if(node.getY()<0 || node.getY()>3) {
				throw new NumberOfFieldsException("Number of fields check", "Map does not conform to 4x8 size");
			}
		}
	}
	
	private static void checkBorders(HalfMap halfMap) throws WaterBordersException {
		int shortLeftSide = 0;
		int shortRightSide = 0;
		int longUpperSide = 0;
		int longDownSide = 0;
		for(HalfMapNode node: halfMap.getMapNodes()) {
			if(node.getX()==0 && node.getTerrain().equals(ETerrain.Water)) {
				shortLeftSide++;
			}
			if(node.getX()==7 && node.getTerrain().equals(ETerrain.Water)) {
				shortRightSide++;
			}
			if(node.getY()==0 && node.getTerrain().equals(ETerrain.Water)) {
				longUpperSide++;
			}
			if(node.getY()==3 && node.getTerrain().equals(ETerrain.Water)) {
				longDownSide++;
			}
		}
		if(shortLeftSide > 1 || shortRightSide > 1) {
			throw new WaterBordersException("Water Borders check", "More than 1 water field on the short side of the map");
		}
		if(longUpperSide > 3 || longDownSide > 3) {
			throw new WaterBordersException("Water Borders check", "More than 3 water fields on the long side of the map");
		}
	}
	
	private static void checkIsland(HalfMap halfMap) throws IslandException {
		HalfMapNode startingNode = new HalfMapNode();
		for(HalfMapNode node: halfMap.getMapNodes()) {
			if(!node.getTerrain().equals(ETerrain.Water)) {
				startingNode = new HalfMapNode(node.getX(), node.getY(), node.isFortPresent(), node.getTerrain());
				break;
			}
		}
		Set<HalfMapNode> originalHalfMap = new HashSet<>(halfMap.getMapNodes());
		originalHalfMap.removeIf(n -> (n.getTerrain().equals(ETerrain.Water)));
		Set<HalfMapNode> testMap = new HashSet<>();
		floodFill(startingNode, halfMap, testMap);
		if(!originalHalfMap.equals(testMap)) {
			throw new IslandException("Island Check", "HalfMap has island");
		}
	}
	
	//here using floodfill algorithm, idea taken from https://www.geeksforgeeks.org/flood-fill-algorithm-implement-fill-paint/
	private static void floodFill(HalfMapNode node, HalfMap halfMap, Collection<HalfMapNode> visitedNodes) {
		if(!node.getTerrain().equals(ETerrain.Water)) {
			visitedNodes.add(node);
			
			if(node.getX()-1>=0 && !visitedNodes.contains(getNode(node.getX()-1, node.getY(), halfMap))) {
				floodFill(getNode(node.getX()-1, node.getY(), halfMap), halfMap, visitedNodes);
			}
			if(node.getY()-1>=0 && !visitedNodes.contains(getNode(node.getX(), node.getY()-1, halfMap))) {
				floodFill(getNode(node.getX(), node.getY()-1, halfMap), halfMap, visitedNodes);
			}
			if(node.getX()+1<=7 && !visitedNodes.contains(getNode(node.getX()+1, node.getY(), halfMap))) {
				floodFill(getNode(node.getX()+1, node.getY(), halfMap), halfMap, visitedNodes);
			}
			if(node.getY()+1<=3 && !visitedNodes.contains(getNode(node.getX(), node.getY()+1, halfMap))) {
				floodFill(getNode(node.getX(), node.getY()+1, halfMap), halfMap, visitedNodes);
			}
		}
	}
	
	private static HalfMapNode getNode(int x, int y, HalfMap halfMap) {
		HalfMapNode res = new HalfMapNode();
		for(HalfMapNode node: halfMap.getMapNodes()) {
			if(node.getX()==x && node.getY()==y) {
				res = new HalfMapNode(x, y, node.isFortPresent(), node.getTerrain());
			}
		}
		return res;
	}
}
