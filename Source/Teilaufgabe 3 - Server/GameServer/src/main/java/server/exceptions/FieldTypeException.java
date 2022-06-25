package server.exceptions;

public class FieldTypeException extends HalfMapException {

	private static final long serialVersionUID = 1L;

	public FieldTypeException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
