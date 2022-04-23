package client.model;

import java.util.HashMap;
import java.util.Objects;

public class Map {
	
	private HashMap<Coordinates, MapObject> mapField;

	public Map(HashMap<Coordinates, MapObject> mapField) {
		super();
		this.mapField = mapField;
	}

	public HashMap<Coordinates, MapObject> getMapField() {
		return mapField;
	}

	public void setMapField(HashMap<Coordinates, MapObject> mapField) {
		this.mapField = mapField;
	}
	
	public void addObjectOnMapField(Coordinates coordinates, ObjectType object) {
		mapField.get(coordinates).addObjectOnField(object);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mapField);
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
		return Objects.equals(mapField, other.mapField);
	}
	
	
}
