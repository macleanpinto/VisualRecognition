package com.tmt.cognitive.challenge.visualrecognition.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.tmt.cognitive.challenge.visualrecognition.interceptor.ConnnectorLoggingInterceptor;


@Configuration
public class ConnectorRestTemplateConfiguration {

	/**
	 * Creates new RestTemplate Object with Interceptors and request factory
	 * @return RestTemplate
	 */
	@Bean(name="restTemplate")
	public RestTemplate getRestTemplate()
	{

		RestTemplate restTemplate = new RestTemplate();

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new ConnnectorLoggingInterceptor());

		restTemplate.setInterceptors(interceptors);
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		
		return restTemplate;

	
	}
}
