package server.exceptions;

public class TwoPlayersException extends GenericExampleException {
	private static final long serialVersionUID = 1L;

	public TwoPlayersException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
