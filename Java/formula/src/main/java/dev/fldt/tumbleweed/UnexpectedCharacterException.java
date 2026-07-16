package dev.fldt.tumbleweed;

/**
 * 
 * @author François Luc Denhez-Teuton
 * 
 * 
 *
 */

public class UnexpectedCharacterException extends IllegalArgumentException {

	private static final long serialVersionUID = 57L;

	public UnexpectedCharacterException() {
		
	}

	public UnexpectedCharacterException(String message) {
		super(message);
		
	}

	public UnexpectedCharacterException(Throwable cause) {
		super(cause);
		
	}

	public UnexpectedCharacterException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
