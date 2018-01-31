package com.tmt.cognitive.challenge.visualrecognition.exception;

import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.tmt.cognitive.challenge.visualrecognition.entity.VisualRecognitionAdFetchResponse;
import com.tmt.cognitive.challenge.visualrecognition.entity.VisualRecognitionGenericResponse;
import com.tmt.cognitive.challenge.visualrecognition.utility.VisualRecognitionServiceErrors;

@ControllerAdvice
public class ExceptionHandlerController {

	
	@Autowired
	private VisualRecognitionServiceErrors templateServiceErrors;
	
	private String errorCode;

	private String errorMessage;

	private static final Logger LOG = LoggerFactory
			.getLogger(ExceptionHandlerController.class);
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	/**
	 * Handler for for any un-handled exception.
	 * 
	 * @param req
	 * @param ex
	 * @return
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	@ResponseBody
	final VisualRecognitionAdFetchResponse handleUnhandledException(HttpServletRequest req,
			Exception ex) {
		LOG.error("Unhandled exception " + ex.getMessage() + ex.getClass());
		return constructErrorMessage(templateServiceErrors.getErrorCode1(), templateServiceErrors.getErrorMessage1());
	}
	
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(VisualRecognitionServiceDaoException.class)
	@ResponseBody
	public VisualRecognitionAdFetchResponse handleDaoException(
			VisualRecognitionServiceDaoException e) {
		 	this.setErrorCode(e.getErrorCode());
		 	this.setErrorMessage(e.getErrorMessage());
		 	LOG.error("Visual Recognition Service DAO Exception " + e.getMessage() + e.getClass());
			return constructErrorMessage(errorCode, errorMessage);
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(VisualRecognitionServiceException.class)
	@ResponseBody
	public VisualRecognitionAdFetchResponse handleApplicationServiceException(
			VisualRecognitionServiceException e) {
		 	this.setErrorCode(e.getErrorCode());
		 	this.setErrorMessage(e.getErrorMessage());
		 	LOG.error("Visual Recognition Service Application Exception " + e.getMessage() + e.getClass());
			return constructErrorMessage(errorCode, errorMessage);
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(InterruptedException.class)
	@ResponseBody
	public VisualRecognitionAdFetchResponse handleInterruptedException(
			InterruptedException e) {
		 	this.setErrorCode(templateServiceErrors.getErrorCode1());
		 	this.setErrorMessage(e.getMessage());
		 	LOG.error("Visual Recognition Service Exception " + e.getMessage() + e.getClass());
			return constructErrorMessage(errorCode, errorMessage);
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(ExecutionException.class)
	@ResponseBody
	public VisualRecognitionAdFetchResponse handleExecutionException(
			ExecutionException e) {
		 	this.setErrorCode(templateServiceErrors.getErrorCode1());
		 	this.setErrorMessage(e.getMessage());
		 	LOG.error("Visual Recognition Services Application Exception " + e.getMessage() + e.getClass());
			return constructErrorMessage(errorCode, errorMessage);
	}
	
	public VisualRecognitionAdFetchResponse constructErrorMessage(String errorCode,
			String errorMessage) {
		VisualRecognitionAdFetchResponse responseJson = new VisualRecognitionAdFetchResponse();
		responseJson.setErrorCode(errorCode);
		responseJson.setErrorMessage(errorMessage);
		return responseJson;
	}
}
