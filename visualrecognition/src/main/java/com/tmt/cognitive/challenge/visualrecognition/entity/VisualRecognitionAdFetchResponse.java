package com.tmt.cognitive.challenge.visualrecognition.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"adurl",
"correlationid"
})
public class VisualRecognitionAdFetchResponse extends VisualRecognitionGenericResponse {
	
	@JsonProperty("adurl")
	private String adUrl;
	
	@JsonProperty("correlationid")
	private Long correlationid;
	
	@JsonProperty("adurl")
	public String getAdUrl() {
		return adUrl;
	}

	@JsonProperty("adurl")
	public void setAdUrl(String adUrl) {
		this.adUrl = adUrl;
	}
	
	@JsonProperty("correlationid")
	public Long getCorrelationid() {
		return correlationid;
	}

	@JsonProperty("correlationid")
	public void setCorrelationid(Long correlationid) {
		this.correlationid = correlationid;
	}



}

