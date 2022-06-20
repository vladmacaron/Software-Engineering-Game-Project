package server.exceptions;

public class NumberOfPlayersException extends GenericExampleException {
	private static final long serialVersionUID = 1L;

	public NumberOfPlayersException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
