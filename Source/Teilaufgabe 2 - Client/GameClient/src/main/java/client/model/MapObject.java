package client.model;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			Logger logger = LoggerFactory.getLogger(MapObject.class);
			logger.error("Object " + object.toString() + " is already on Field");
			throw new RuntimeException("Object " + object.toString() + " is already on Field");
		} else {
			objectsOnField.add(object);
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
