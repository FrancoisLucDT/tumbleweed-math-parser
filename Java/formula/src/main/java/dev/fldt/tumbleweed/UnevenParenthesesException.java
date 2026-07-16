package dev.fldt.tumbleweed;

/**
 *
 * @author François Luc Denhez-Teuton
 *
 *
 *
 */

public class UnevenParenthesesException extends IllegalArgumentException {

	static final long serialVersionUID=65L;

	public UnevenParenthesesException() {
		super();
	}

	public UnevenParenthesesException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnevenParenthesesException(String s) {
		super(s);
	}

	public UnevenParenthesesException(Throwable cause) {
		super(cause);
	}

}
