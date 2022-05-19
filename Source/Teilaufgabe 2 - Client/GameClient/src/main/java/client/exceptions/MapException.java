package client.exceptions;

public class MapException extends Exception {
	String description;

	public MapException(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Setting Map excpetion: " + description;
	}
	
}
