package com.tmt.cognitive.challenge.visualrecognition.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VisualRecognitionServiceErrors {
	
	@Value("${API_SYS_ERR_001_ID}")
	private String errorCode1;

	@Value("${API_SYS_ERR_001_MSG}")
	private String errorMessage1;
	
	public String getErrorCode1() {
		return errorCode1;
	}

	public void setErrorCode1(String errorCode1) {
		this.errorCode1 = errorCode1;
	}

	public String getErrorMessage1() {
		return errorMessage1;
	}

	public void setErrorMessage1(String errorMessage1) {
		this.errorMessage1 = errorMessage1;
	}

}
