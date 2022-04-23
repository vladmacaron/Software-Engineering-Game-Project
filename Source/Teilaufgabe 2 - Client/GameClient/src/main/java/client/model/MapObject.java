package client.model;

import java.util.List;
import java.util.Objects;

public class MapObject {
	private TerrainType terrainType;
	private List<ObjectType> objectsOnField;
	
	public MapObject(TerrainType terrainType, List<ObjectType> objectsOnField) {
		super();
		this.terrainType = terrainType;
		this.objectsOnField = objectsOnField;
	}

	public TerrainType getTerrainType() {
		return terrainType;
	}

	public List<ObjectType> getObjectsOnField() {
		return objectsOnField;
	}
	
	public void addObjectOnField(ObjectType object) {
		if (objectsOnField.contains(object)) {
			//TODO throw error
		} else {
			objectsOnField.add(object);
		}
	}
	
	public void removeObjectFromField(ObjectType object) {
		if (!objectsOnField.remove(object)) {
			//TODO throw error
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(objectsOnField, terrainType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapObject other = (MapObject) obj;
		return Objects.equals(objectsOnField, other.objectsOnField) && terrainType == other.terrainType;
	}
	
	
}
