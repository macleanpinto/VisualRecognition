package com.tmt.cognitive.challenge.visualrecognition.exception;



public abstract class VisualRecognitionException extends Exception {

	private static final long serialVersionUID = 1L;

	protected String errorCode;

	protected String errorMessage;

	protected String httpErrorCode;

	public abstract String getErrorCode();

	public abstract String getErrorMessage();

	public abstract String getHttpErrorCode();

	
	public VisualRecognitionException() {

	}

	public VisualRecognitionException(String errorCode, String errorMessage, String httpErrorCode) {
		super(errorMessage);
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.httpErrorCode = httpErrorCode;

	}

	/**
	 * @param message
	 */
	public VisualRecognitionException(String message) {
		super(message);

	}

	/**
	 * @param cause
	 */
	public VisualRecognitionException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public VisualRecognitionException(String message, Throwable cause) {
		super(message, cause);

	}

	
}
