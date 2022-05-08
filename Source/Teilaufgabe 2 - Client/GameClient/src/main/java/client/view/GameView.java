package client.view;

import java.beans.PropertyChangeListener;
import java.util.HashMap;

import client.model.Coordinates;
import client.model.Map;
import client.model.MapObject;
import client.model.ObjectType;
import client.model.TerrainType;

public class GameView {
	public static final String GREEN_BACKGROUND = "\u001B[42m";
	public static final String BLUE_BACKGROUND = "\u001B[44m";
	public static final String GREY_BACKGROUND = "\u001b[37m";
	public static final String COLOR_RESET = "\u001B[0m";
	
	private Map gameMap;
	
	public GameView(Map gameMap) {
		this.gameMap = gameMap;
		gameMap.addPropertyChangeListener(mapChangedListener);
	}
	
	final PropertyChangeListener mapChangedListener = event -> {
		Object model = event.getSource();
		Object newValue = event.getNewValue();
	    	
		System.out.println("Display of the changed value:" + newValue);

		if(model instanceof Map) {
			Map castedModel = (Map)model;
			printCurrentMap(castedModel);
		}	
	};
	
	public void printCurrentMap(Map gameMap) {
		for(int y = 0; y <= gameMap.getMaxRow(); y++) {
			for(int x = 0; x <= gameMap.getMaxColumn(); x++) {
				MapObject mapObject = gameMap.getMapObject(new Coordinates(x, y));
				System.out.print("|");
				printTerrain(mapObject.getTerrainType());
				if(mapObject.getObjectsOnField().isEmpty()) {
					System.out.print("___");
				} else {
					switch(mapObject.getObjectsOnField().size()) {
					case 1:
						System.out.print("_");
						mapObject.getObjectsOnField().forEach((object) -> printObject(object));
						System.out.print("_");
						break;
					case 2:
						mapObject.getObjectsOnField().forEach((object) -> printObject(object));
						System.out.print("_");
						break;
					case 3:
						mapObject.getObjectsOnField().forEach((object) -> printObject(object));
						break;
					default:
						mapObject.getObjectsOnField().forEach((object) -> printObject(object));
						break;
					}
				}
				System.out.print(COLOR_RESET);
			}
			System.out.println("");
		}
	}
	
	private void printTerrain(TerrainType terrainType) {
		switch(terrainType) {
		case GRASS:
			System.out.print(GREEN_BACKGROUND);
			break;
		case MOUNTAIN:
			System.out.print(GREY_BACKGROUND);
			break;
		case WATER:
			System.out.print(BLUE_BACKGROUND);
			break;
		default:
			break;
		}
	}
	
	private void printObject(ObjectType objectType) {
		switch(objectType) {
		case CASTLE:
			System.out.print("^");
			break;
		case ENEMY:
			System.out.print("E");
			break;
		case ENEMY_CASTLE:
			//add color
			System.out.print("^");
			break;
		case ENEMY_TREASURE:
			//add color
			System.out.print("$");
			break;
		case PLAYER:
			System.out.print("P");
			break;
		case TREASURE:
			System.out.print("$");
			break;
		default:
			break;
		}
	}
}
