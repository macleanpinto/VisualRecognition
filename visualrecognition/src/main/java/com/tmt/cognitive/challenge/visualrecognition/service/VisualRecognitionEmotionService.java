package com.tmt.cognitive.challenge.visualrecognition.service;

import java.util.List;
import java.util.Map;

import com.tmt.cognitive.challenge.visualrecognition.entity.VisualRecognitionAdFetchResponse;
import com.tmt.cognitive.challenge.visualrecognition.exception.VisualRecognitionServiceException;

public interface VisualRecognitionEmotionService {

	public VisualRecognitionAdFetchResponse updateTransactionBasedOnEmotion(Map<String,Object> requestPayload);
	public Object fetchEmotionAttributest(Map<String,Object> imageRequest) throws VisualRecognitionServiceException;
}
