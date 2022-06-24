package map;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromServer.EFortState;
import MessagesBase.MessagesFromServer.EPlayerPositionState;
import MessagesBase.MessagesFromServer.ETreasureState;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.FullMapNode;

public class FullMapCreator {
	
	public static AbstractMap.SimpleEntry<FullMap, String>  createFullMap(List<HalfMap> playersHalfMaps) {
		boolean mapIsSquare = new Random().nextBoolean();
		int XShiftBy = 0;
		int YShiftBy = 0;
		int firstMap = new Random().nextInt(2);
		int secondMap = 0;
		if(firstMap==0) {
			secondMap=1;
		}
		
		Set<FullMapNode> mapNodes = new HashSet<>();
		
		for(HalfMapNode node: playersHalfMaps.get(firstMap).getMapNodes()) {
			if(node.isFortPresent()) {
				mapNodes.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.MyPlayerPosition, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.MyFortPresent, 
											node.getX(), 
											node.getY()));
			} else {
				mapNodes.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.NoPlayerPresent, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.NoOrUnknownFortState, 
											node.getX(), 
											node.getY()));
			}
		}
		
		if(mapIsSquare) {
			XShiftBy = 8;
		} else {
			YShiftBy = 4;
		}
		
		for(HalfMapNode node: playersHalfMaps.get(secondMap).getMapNodes()) {
			if(node.isFortPresent()) {
				mapNodes.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.EnemyPlayerPosition, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.EnemyFortPresent, 
											node.getX()+XShiftBy, 
											node.getY()+YShiftBy));
			} else {
				mapNodes.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.NoPlayerPresent, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.NoOrUnknownFortState, 
											node.getX()+XShiftBy, 
											node.getY()+YShiftBy));
			}
		}
		
		/*System.out.println("--------------");
		for(FullMapNode node: mapNodes) {
			System.out.println(node.toString());
		}
		System.out.println("--------------");*/
		
		return new AbstractMap.SimpleEntry<FullMap, String>(new FullMap(mapNodes), playersHalfMaps.get(firstMap).getUniquePlayerID());
	}

}
