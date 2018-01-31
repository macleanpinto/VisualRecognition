package com.tmt.cognitive.challenge.visualrecognition.utility;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.tmt.cognitive.challenge.visualrecognition.exception.VisualRecognitionServiceException;


@SuppressWarnings("deprecation")
public class HttpUtil {
	final static Logger LOG = Logger.getLogger(HttpUtil.class);

	public static ResponseEntity<Object> postRequest(RestTemplate restTemplate, HttpMethod method, String url, String body) throws VisualRecognitionServiceException {

		LOG.info("Request Body "+body);
		ResponseEntity<Object> response = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("ACCEPT", MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<String> entity = new HttpEntity<String>(body, headers);

			response = restTemplate.exchange(url, method, entity, Object.class);
			LOG.info("Response Body "+response.getBody());
			return response;
		} catch (Exception e) {
			if (e instanceof HttpClientErrorException) {
				HttpClientErrorException exception = (HttpClientErrorException) e;
				LOG.info("Exception while invoking URL : " + url);
				LOG.info("Response for this Excpetion : " + exception.getResponseBodyAsString());
				throw e;
			} else {
					throw e;
			}
		}
		
	}
	
}