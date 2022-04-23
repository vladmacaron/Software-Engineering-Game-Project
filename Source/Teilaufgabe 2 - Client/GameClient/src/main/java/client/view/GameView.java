package client.view;

import java.beans.PropertyChangeListener;

import client.model.Map;

public class GameView {
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
	
	private void printCurrentMap(Map gameMap) {
		
	}
}
