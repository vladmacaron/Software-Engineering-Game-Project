package server.exceptions;

public class PlayerIdException extends GenericExampleException {
	private static final long serialVersionUID = 1L;

	public PlayerIdException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
