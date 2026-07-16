package dev.fldt.tumbleweed;

/**
 * 
 * @author François Luc Denhez-Teuton
 * 
 * 
 *
 */

public class UnexpectedTokenException extends IllegalArgumentException {
	
	private static final long serialVersionUID = 58L;

	public UnexpectedTokenException() {
		super();
	}

	public UnexpectedTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedTokenException(String message) {
		super(message);
	}

	public UnexpectedTokenException(Throwable cause) {
		super(cause);
	}

}
