package client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Objects;

import client.exceptions.MapException;

public class Map {
	
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
	private HashMap<Coordinates, MapObject> mapFields;

	public Map(HashMap<Coordinates, MapObject> mapFields) {
		this.mapFields = mapFields;
	}
	
	public Map() {
		this.mapFields = new HashMap<Coordinates, MapObject>();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		//enables to register new listeners
		changes.addPropertyChangeListener(listener);
	}
	
	public Map(Map other) {
		this.mapFields = new HashMap<Coordinates, MapObject>();
		if(!other.mapFields.isEmpty()) {
			for(int x=0; x<other.getMaxColumn(); x++) {
				for(int y=0; y<other.getMaxRow(); y++) {
					Coordinates tempCoord = new Coordinates(x, y);
					this.mapFields.put(tempCoord, other.getMapObject(tempCoord));
				}
			}
		}
	}
	
	public void setMap(Map map) throws MapException {
		try {
			Map beforeChange = new Map(this);
			this.mapFields = map.getMapFields();
			changes.firePropertyChange("map", beforeChange, this);
		} catch(Exception e) {
			throw new MapException(e.getMessage() + "failed to set Map correctly");
		}
	}

	public HashMap<Coordinates, MapObject> getMapFields() {
		return mapFields;
	}

	public void setMapField(HashMap<Coordinates, MapObject> mapFields) {
		this.mapFields = mapFields;
	}
	
	public void addObjectOnMapField(Coordinates coordinates, ObjectType object) {
		mapFields.get(coordinates).addObjectOnField(object);
	}
	
	public MapObject getMapObject(Coordinates coord) {
		return mapFields.get(coord);
	}
	
	public Coordinates getPlayerPosition() {
		Coordinates res = new Coordinates(-1,-1);
		for (HashMap.Entry<Coordinates, MapObject> field : mapFields.entrySet()) {
			for(ObjectType objects : field.getValue().getObjectsOnField()) {
				if(objects.equals(ObjectType.PLAYER)) {
					res = field.getKey();
				}
			}
		}
		return res;
	}
	
	public int getMaxRow() {
		Coordinates maxKey = null;
        for (Coordinates key : mapFields.keySet()) {
            if (maxKey == null || key.getY() > maxKey.getY()) {
                maxKey = key;
            }
        }
        if(maxKey == null) {
        	throw new RuntimeException("maxKey to find max row is null, mapFields are empty");
        }
        return maxKey.getY();
	}
	
	public int getMaxColumn() {
		Coordinates maxKey = null;
        for (Coordinates key : mapFields.keySet()) {
            if (maxKey == null || key.getX() > maxKey.getX()) {
                maxKey = key;
            }
        }
        if(maxKey == null) {
        	throw new RuntimeException("maxKey to find max column is null, mapFields are empty");
        }
        return maxKey.getX();
	}

	@Override
	public int hashCode() {
		return Objects.hash(mapFields);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Map other = (Map) obj;
		return Objects.equals(mapFields, other.mapFields);
	}
	
	
}
