package net.patrykczarnik.commands;

public class ExecutionException extends Exception {
	private static final long serialVersionUID = 1L;

	public ExecutionException() {
		super();
	}

	public ExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExecutionException(String message) {
		super(message);
	}

	public ExecutionException(Throwable cause) {
		super(cause);
	}

}
