package com.tmt.cognitive.challenge.visualrecognition.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tmt.cognitive.challenge.visualrecognition.entity.VisualRecognitionAdFetchResponse;
import com.tmt.cognitive.challenge.visualrecognition.exception.VisualRecognitionServiceDaoException;
import com.tmt.cognitive.challenge.visualrecognition.exception.VisualRecognitionServiceException;
import com.tmt.cognitive.challenge.visualrecognition.serviceImpl.VisualRecognitionEmotionServiceImpl;
import com.tmt.cognitive.challenge.visualrecognition.serviceImpl.VisualRecognitionServiceImpl;

@RestController
@RequestMapping("/v1/apis")
public class VisualRecognitionController {
	final static Logger LOG = Logger.getLogger(VisualRecognitionController.class);

	@Autowired
	VisualRecognitionServiceImpl visualRecognitionServiceImpl;

	@Autowired
	VisualRecognitionEmotionServiceImpl visualRecognitionEmotionServiceImpl;
	
	@RequestMapping(value="/advertisements/face", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public VisualRecognitionAdFetchResponse getAdsBasedOnAgeGender(@RequestHeader HttpHeaders reqHeaders,@RequestBody List<Map<String,Object>> requestPayload) throws VisualRecognitionServiceDaoException,VisualRecognitionServiceException,Exception {
		LOG.debug("Skf Employee Carrier getRolesBasedOnGradesList Request Payload :" + requestPayload);
		VisualRecognitionAdFetchResponse response = visualRecognitionServiceImpl.getAdUrlBasedOnAgeGender(requestPayload);
		return response;
	}
	
	@RequestMapping(value="/emotions/face", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public VisualRecognitionAdFetchResponse updateTransactionBasedOnEmotion(@RequestHeader HttpHeaders reqHeaders,@RequestBody Map<String,Object> requestPayload) throws VisualRecognitionServiceDaoException,VisualRecognitionServiceException,Exception {
		LOG.debug("Skf Employee Carrier getRolesBasedOnGradesList Request Payload :" + requestPayload);
		VisualRecognitionAdFetchResponse response = visualRecognitionEmotionServiceImpl.updateTransactionBasedOnEmotion(requestPayload);
		return response;
	}
	
	@RequestMapping(value="/advertisements", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object recieveAdvertisementSearchRequest(@RequestHeader HttpHeaders reqHeaders,@RequestBody Map<String,Object> requestPayload) throws VisualRecognitionServiceDaoException,VisualRecognitionServiceException,Exception {
		LOG.debug("Skf Employee Carrier getRolesBasedOnGradesList Request Payload :" + requestPayload);
		Object response = visualRecognitionServiceImpl.fetchFaceAttributes(requestPayload);
		return response;
	}
	
	@RequestMapping(value="/emotions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody	
	public Object recieveEmotionsUpdateRequest(@RequestHeader HttpHeaders reqHeaders,@RequestBody Map<String,Object> requestPayload) throws VisualRecognitionServiceDaoException,VisualRecognitionServiceException,Exception {
		LOG.debug("Skf Employee Carrier getRolesBasedOnGradesList Request Payload :" + requestPayload);
		Object response = visualRecognitionEmotionServiceImpl.fetchEmotionAttributest(requestPayload);
		return response;
	}
	
	
}
