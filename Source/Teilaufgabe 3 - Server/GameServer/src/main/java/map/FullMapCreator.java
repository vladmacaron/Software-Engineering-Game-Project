package map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	
	public static Map<String, FullMap>  createFullMap(List<HalfMap> playersHalfMaps) {
		boolean mapIsSquare = new Random().nextBoolean();
		int XShiftBy = 0;
		int YShiftBy = 0;
		int firstMap = new Random().nextInt(2);
		int secondMap = 0;
		if(firstMap==0) {
			secondMap=1;
		}
		
		Map<String, FullMap> fullMaps = new HashMap<>();
		Set<FullMapNode> mapNodesFirstPlayer = new HashSet<>();
		Set<FullMapNode> mapNodesSecondPlayer = new HashSet<>();
		
		//populating map nodes for both players, visibility of certain fields will be limited this way
		// i.e. castle not visible for the second player
		for(HalfMapNode node: playersHalfMaps.get(firstMap).getMapNodes()) {
			if(node.isFortPresent()) {
				mapNodesFirstPlayer.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.MyPlayerPosition, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.MyFortPresent, 
											node.getX(), 
											node.getY()));
			} else {
				mapNodesFirstPlayer.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.NoPlayerPresent, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.NoOrUnknownFortState, 
											node.getX(), 
											node.getY()));
			}
		}
		for(HalfMapNode node: playersHalfMaps.get(firstMap).getMapNodes()) {
				mapNodesSecondPlayer.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.NoPlayerPresent, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.NoOrUnknownFortState, 
											node.getX(), 
											node.getY()));
		}
		
		if(mapIsSquare) {
			YShiftBy = 4;
		} else {
			XShiftBy = 8;
		}
		
		for(HalfMapNode node: playersHalfMaps.get(secondMap).getMapNodes()) {
				mapNodesFirstPlayer.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.NoPlayerPresent, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.NoOrUnknownFortState, 
											node.getX()+XShiftBy, 
											node.getY()+YShiftBy));
		}
		for(HalfMapNode node: playersHalfMaps.get(secondMap).getMapNodes()) {
			if(node.isFortPresent()) {
				mapNodesSecondPlayer.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.MyPlayerPosition, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.MyFortPresent, 
											node.getX()+XShiftBy, 
											node.getY()+YShiftBy));
			} else {
				mapNodesSecondPlayer.add(new FullMapNode(node.getTerrain(),
											EPlayerPositionState.NoPlayerPresent, 
											ETreasureState.NoOrUnknownTreasureState, 
											EFortState.NoOrUnknownFortState, 
											node.getX()+XShiftBy, 
											node.getY()+YShiftBy));
			}
		}
		
		fullMaps.put(playersHalfMaps.get(firstMap).getUniquePlayerID(), new FullMap(mapNodesFirstPlayer));
		fullMaps.put(playersHalfMaps.get(secondMap).getUniquePlayerID(), new FullMap(mapNodesSecondPlayer));
		
		return fullMaps;
	}
}
