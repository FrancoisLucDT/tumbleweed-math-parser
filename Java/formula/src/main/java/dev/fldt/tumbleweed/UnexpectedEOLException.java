package dev.fldt.tumbleweed;

/**
 * 
 * @author François Luc Denhez-Teuton
 * 
 * 
 *
 */

public class UnexpectedEOLException extends IllegalArgumentException {

	static final long serialVersionUID = 55L;

	public UnexpectedEOLException() {
		super();
	}

	public UnexpectedEOLException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedEOLException(String message) {
		super(message);
	}

	public UnexpectedEOLException(Throwable cause) {
		super(cause);
	}
	
}
