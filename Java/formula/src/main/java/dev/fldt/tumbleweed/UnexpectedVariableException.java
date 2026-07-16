package dev.fldt.tumbleweed;

/**
 * 
 * @author François Luc Denhez-Teuton
 * 
 * 
 *
 */

public class UnexpectedVariableException extends IllegalArgumentException {

	private static final long serialVersionUID = 59L;
	
	public UnexpectedVariableException() {
		super();
	}

	public UnexpectedVariableException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedVariableException(String message) {
		super(message);
	}

	public UnexpectedVariableException(Throwable cause) {
		super(cause);
	}

}
