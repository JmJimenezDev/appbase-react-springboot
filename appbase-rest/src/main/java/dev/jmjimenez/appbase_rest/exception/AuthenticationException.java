package dev.jmjimenez.appbase_rest.exception;

public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = -928258897969813247L;

	public AuthenticationException(String message) {
		super(message);
	}
}
