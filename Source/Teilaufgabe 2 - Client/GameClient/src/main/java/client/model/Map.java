package client.model;

import java.util.HashMap;
import java.util.Objects;

public class Map {
	
	private HashMap<Coordinates, MapObject> mapFields;

	public Map(HashMap<Coordinates, MapObject> mapFields) {
		super();
		this.mapFields = mapFields;
	}

	public HashMap<Coordinates, MapObject> getMapField() {
		return mapFields;
	}

	public void setMapField(HashMap<Coordinates, MapObject> mapFields) {
		this.mapFields = mapFields;
	}
	
	public void addObjectOnMapField(Coordinates coordinates, ObjectType object) {
		mapFields.get(coordinates).addObjectOnField(object);
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
