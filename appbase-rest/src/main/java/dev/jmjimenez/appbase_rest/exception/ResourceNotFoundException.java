package dev.jmjimenez.appbase_rest.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7358258725364740521L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
