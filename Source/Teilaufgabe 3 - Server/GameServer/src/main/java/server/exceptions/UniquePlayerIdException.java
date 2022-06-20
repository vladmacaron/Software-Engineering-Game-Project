package server.exceptions;

public class UniquePlayerIdException extends GenericExampleException {
	private static final long serialVersionUID = 1L;

	public UniquePlayerIdException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
