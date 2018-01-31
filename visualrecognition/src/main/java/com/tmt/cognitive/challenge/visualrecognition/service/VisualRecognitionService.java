package com.tmt.cognitive.challenge.visualrecognition.service;

import java.util.List;
import java.util.Map;

import com.tmt.cognitive.challenge.visualrecognition.entity.VisualRecognitionAdFetchResponse;
import com.tmt.cognitive.challenge.visualrecognition.exception.VisualRecognitionServiceException;

public interface VisualRecognitionService {

	VisualRecognitionAdFetchResponse getAdUrlBasedOnAgeGender(List<Map<String,Object>> requestPayload) throws VisualRecognitionServiceException;
	Object fetchFaceAttributes(Map<String,Object> imageRequest) throws VisualRecognitionServiceException;
	
}
