package server.exceptions;

public class GameIdException extends GenericExampleException {
	private static final long serialVersionUID = 1L;

	public GameIdException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
