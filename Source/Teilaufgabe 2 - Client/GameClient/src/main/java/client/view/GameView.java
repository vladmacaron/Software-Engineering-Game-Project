package client.view;

import client.model.Map;

public class GameView {
	private Map gameMap;
	
	public PCView(PCModel model, PCController controller) {
		this.controller = controller;
		model.addPropertyChangeListener(modelChangedListener);
	}
	
	// using a lambda expression or anonymous classes to define the handler of an event change listener
		// here, we show how to use lambda expressions
	    final PropertyChangeListener modelChangedListener = event -> {

	    	Object model = event.getSource();
	    	Object newValue = event.getNewValue();
	    	
			System.out.println("Display of the changed value:" + newValue);

			if(model instanceof PCModel)
			{
				System.out.println("You even get the whole model.");
				
				PCModel castedModel = (PCModel)model;
				System.out.println("After casting I can access the data of the model:" + castedModel.getSomeData());
			}
	    };
}
