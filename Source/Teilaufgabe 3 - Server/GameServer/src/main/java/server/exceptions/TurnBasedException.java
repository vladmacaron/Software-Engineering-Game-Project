package server.exceptions;

public class TurnBasedException extends GenericExampleException {

	private static final long serialVersionUID = 1L;

	public TurnBasedException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
