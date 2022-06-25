package server.exceptions;

public class NumberOfFieldsException extends HalfMapException {
	private static final long serialVersionUID = 1L;

	public NumberOfFieldsException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
