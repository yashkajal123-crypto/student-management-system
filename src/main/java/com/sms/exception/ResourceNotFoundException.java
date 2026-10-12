package com.sms.exception;

/**
 * Thrown when a student / course / enrollment is requested
 * with an id that does not exist. Converted to the 404 page
 * by the {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
