package server.exceptions;

public class IslandException extends HalfMapException {
	private static final long serialVersionUID = 1L;

	public IslandException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
