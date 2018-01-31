package com.tmt.cognitive.challenge.visualrecognition.dao;

import java.util.List;

import com.tmt.cognitive.challenge.visualrecognition.entity.AdsMappingDetails;
import com.tmt.cognitive.challenge.visualrecognition.entity.ScoreMappingDetails;
import com.tmt.cognitive.challenge.visualrecognition.vo.Userproductscoremapping;
import com.tmt.cognitive.challenge.visualrecognition.vo.Visualrecognitionaudittransaction;

public interface VisualRecognitionDao {

	public ScoreMappingDetails getScoreMapping(Double age,String gender);
	
	public AdsMappingDetails getAdsMapping(Integer userProfileId, Integer productId);
	
	public Long insertTransaction(Visualrecognitionaudittransaction visualrecognitionaudittransaction);
	
	public boolean updateTransaction(String result, Long transactionid);
	
	public List<Userproductscoremapping> getScoreMappings(Long userProfileId);

	public Visualrecognitionaudittransaction getTransaction(Long transactionid);

	public void updateScoreMapping(Userproductscoremapping userproductscoremapping);
	
}
