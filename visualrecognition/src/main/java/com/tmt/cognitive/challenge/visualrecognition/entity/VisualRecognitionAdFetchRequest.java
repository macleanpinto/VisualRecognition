package com.tmt.cognitive.challenge.visualrecognition.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"miniumage",
"maximumage",
"gender"
})
public class VisualRecognitionAdFetchRequest {

	@JsonProperty("miniumage")
	private int miniumage;
	
	@JsonProperty("maximumage")
	private int maximumage;
	
	@JsonProperty("gender")
	private String gender;
	
	@JsonProperty("miniumage")
	public int getMiniumage() {
		return miniumage;
	}
	
	@JsonProperty("miniumage")
	public void setMiniumage(int miniumage) {
		this.miniumage = miniumage;
	}
	
	@JsonProperty("maximumage")
	public int getMaximumage() {
		return maximumage;
	}
	
	@JsonProperty("maximumage")
	public void setMaximumage(int maximumage) {
		this.maximumage = maximumage;
	}
	
	@JsonProperty("gender")
	public String getGender() {
		return gender;
	}
	
	@JsonProperty("gender")
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
}
