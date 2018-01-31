package com.tmt.cognitive.challenge.visualrecognition.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class ConnnectorLoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger log = LoggerFactory.getLogger(ConnnectorLoggingInterceptor.class);


	private StringBuffer getRequestDetails(HttpRequest request, byte[] body) throws IOException {

		StringBuffer sw= new StringBuffer();
		sw.append("\n/***************BEGIN REQUEST********************/\n\n");
		HttpHeaders reqHeaxeders = request.getHeaders();
		Iterator<Map.Entry<String,List<String>>> headerIterator = reqHeaxeders.entrySet().iterator();
		sw.append("Headers: \n");
		while(headerIterator.hasNext())
		{
			Map.Entry<String,List<String>> entry =headerIterator.next();
			sw.append("Key: "+entry.getKey()+" ,Value: "+entry.getValue()+"\n");
		}
		sw.append("Method: "+request.getMethod()+"\n");
		sw.append("URI: "+request.getURI()+"\n");
		String str = new String(body, StandardCharsets.UTF_8);
		sw.append("Request Body: \n"+str+"\n");
		sw.append("\n/**************END REQUEST*********************/");
		return sw;

	}
	private StringBuffer getResponseDetails(ClientHttpResponse response) throws IOException {
		StringBuffer sw= new StringBuffer();
		sw.append("\n/***************BEGIN RESPONSE********************/\n\n");
		sw.append("Status:"+response.getStatusCode()+"\n");
		BufferedReader bf = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
		String data =null;
		Iterator<Map.Entry<String,List<String>>> headerIterator = response.getHeaders().entrySet().iterator();
		sw.append("Headers: \n");
		while(headerIterator.hasNext())
		{
			Map.Entry<String,List<String>> entry =headerIterator.next();
			sw.append("Key: "+entry.getKey()+" ,Value: "+entry.getValue()+"\n");
		}
		sw.append("Response Body:\n");
		while((data = bf.readLine())!= null)
		{
			sw.append(data+"\n");
		}
		sw.append("\n/***************END RESPONSE********************/");
		return sw;
	}
	private void logRequestAndResposne(HttpRequest request, byte[] body,ClientHttpResponse response) throws IOException
	{
		StringBuffer sw= new StringBuffer();
		sw.append(getRequestDetails(request,body));
		sw.append(getResponseDetails(response));
		log.debug(sw.toString());
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)throws IOException {

		log.debug(getRequestDetails(request,body).toString());
		ClientHttpResponse response = execution.execute(request, body);
		logRequestAndResposne(request,body,response);
		return response;
	}
}
