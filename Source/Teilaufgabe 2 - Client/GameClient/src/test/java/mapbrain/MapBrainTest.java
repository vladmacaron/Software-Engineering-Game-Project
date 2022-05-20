package mapbrain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import client.ai.MapBrain;
import client.model.Coordinates;
import client.model.Map;
import client.model.MovementType;

class MapBrainTest {

	@Test
	void mapbrain_test() {
		MapBrain testBrain = Mockito.mock(MapBrain.class);
		Coordinates coord = Mockito.mock(Coordinates.class);
		
		testBrain.findTreasure();
		
		Mockito.verify(testBrain).findTreasure();
		
		testBrain.findCastle();
		
		Mockito.verify(testBrain).findCastle();
		
		testBrain.addVisitedPoint(coord);
		
		Mockito.verify(testBrain).addVisitedPoint(coord);
	}

}
