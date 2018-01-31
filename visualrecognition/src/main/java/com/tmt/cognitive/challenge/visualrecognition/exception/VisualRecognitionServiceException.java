package com.tmt.cognitive.challenge.visualrecognition.exception;

public class VisualRecognitionServiceException extends VisualRecognitionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VisualRecognitionServiceException() {
	}

	public VisualRecognitionServiceException(String errorCode, String errorMessage, String httpErrorCode) {
		super(errorCode, errorMessage, httpErrorCode);

	}

	
	@Override
	public String getErrorCode() {

		return errorCode;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}
	
	@Override
	public String getHttpErrorCode() {

		return httpErrorCode;
	}

}
