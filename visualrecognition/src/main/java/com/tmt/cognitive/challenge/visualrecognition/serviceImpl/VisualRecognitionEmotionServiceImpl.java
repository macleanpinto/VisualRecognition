package com.tmt.cognitive.challenge.visualrecognition.serviceImpl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmt.cognitive.challenge.visualrecognition.dao.VisualRecognitionDaoImpl;
import com.tmt.cognitive.challenge.visualrecognition.entity.AdsMappingDetails;
import com.tmt.cognitive.challenge.visualrecognition.entity.ScoreMappingDetails;
import com.tmt.cognitive.challenge.visualrecognition.entity.VisualRecognitionAdFetchResponse;
import com.tmt.cognitive.challenge.visualrecognition.exception.VisualRecognitionServiceException;
import com.tmt.cognitive.challenge.visualrecognition.service.VisualRecognitionEmotionService;
import com.tmt.cognitive.challenge.visualrecognition.utility.HttpUtil;
import com.tmt.cognitive.challenge.visualrecognition.vo.Commercialproductsprofile;
import com.tmt.cognitive.challenge.visualrecognition.vo.Userproductadsmapping;
import com.tmt.cognitive.challenge.visualrecognition.vo.Userproductscoremapping;
import com.tmt.cognitive.challenge.visualrecognition.vo.Visualrecognisionuserprofiles;
import com.tmt.cognitive.challenge.visualrecognition.vo.Visualrecognitionaudittransaction;

import sun.misc.BASE64Decoder;

@Service
public class VisualRecognitionEmotionServiceImpl implements VisualRecognitionEmotionService{

	final static Logger LOG = Logger.getLogger(VisualRecognitionEmotionServiceImpl.class);
	@Value("${emotionapi.subscriptionkey}")
	private String subscriptionKey;
	
	@Value("${emotionapi.baseuri}")
	private String emotionsUriBase;
	
	
	private int imageCount = 0;
	@Value("#{'${visualrecognition.positive}'.split(',')}")
	private List<String> positiveFactors;
	
	@Value("#{'${visualrecognition.negative}'.split(',')}")
	private List<String> negativeFactors;
	
	@Value("#{'${visualrecognition.neutral}'.split(',')}")
	private List<String> neutralFactors;
	
	@Autowired
	private VisualRecognitionDaoImpl visualRecognitionDaoImpl;

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public VisualRecognitionAdFetchResponse updateTransactionBasedOnEmotion(Map<String, Object> requestPayload) {
		
		List<Double> postivies = new ArrayList<>();
		List<Double> negatives = new ArrayList<>();
		List<Double> neutrals = new ArrayList<>();
		VisualRecognitionAdFetchResponse adResponse = null;
		Long correlationid = Long.valueOf(String.valueOf((Integer)requestPayload.get("correlationid")));
		List<Map<String,Object>> emotionsList = (List<Map<String, Object>>) requestPayload.get("emotions");
		if(emotionsList!=null && !emotionsList.isEmpty()){
			Map<String,Object> emotionsMap = emotionsList.get(0);
			Map<String,Object> emotionScores = (Map<String, Object>) emotionsMap.get("scores");
			
			for(String positiveFactor : positiveFactors){
				postivies.add((Double)emotionScores.get(positiveFactor));
			}
			
			for(String negativeFactor : negativeFactors){
				negatives.add((Double)emotionScores.get(negativeFactor));
			}
			
			for(String neutralFactor : neutralFactors){
				neutrals.add((Double)emotionScores.get(neutralFactor));
			}
			
			String emotionResult = this.deriveEmotion(postivies, negatives, neutrals);
			if(emotionResult == null){
				adResponse = new VisualRecognitionAdFetchResponse();
				adResponse.setStatus("failure");
			}else if("neutral".equalsIgnoreCase(emotionResult)){
				System.out.println("Emotion Factor - " + emotionResult);
				visualRecognitionDaoImpl.updateTransaction(emotionResult, correlationid);
				adResponse = new VisualRecognitionAdFetchResponse();
				adResponse.setStatus("neutral");
			}else if("positive".equalsIgnoreCase(emotionResult)){
				System.out.println("Emotion Factor - " + emotionResult);
				visualRecognitionDaoImpl.updateTransaction(emotionResult, correlationid);
				adResponse = this.updateScoreMapping(emotionResult, correlationid);
				return adResponse;
			}else if("negative".equalsIgnoreCase(emotionResult)){
				visualRecognitionDaoImpl.updateTransaction(emotionResult, correlationid);
				System.out.println("Emotion Factor - " + emotionResult);
				adResponse = this.updateScoreMapping(emotionResult, correlationid);
				return adResponse;
			}
		}
		return adResponse;
	}
	
	
	private VisualRecognitionAdFetchResponse updateScoreMapping(String emotionResult,Long correlationid){
		
		Visualrecognitionaudittransaction auditTransaction = visualRecognitionDaoImpl.getTransaction(correlationid);
		List<Userproductscoremapping> userProductScoreMapping = visualRecognitionDaoImpl.getScoreMappings(auditTransaction.getVisualrecognisionuserprofiles().getUserprofileid());
		VisualRecognitionAdFetchResponse adFetchResponse = null;
		if("positive".equalsIgnoreCase(emotionResult)){
			for(Userproductscoremapping scoreMapping : userProductScoreMapping){
				if(scoreMapping.getScoremappingid().equals(auditTransaction.getUserproductscoremapping().getScoremappingid())){
					scoreMapping.setScore(scoreMapping.getScore()<10?scoreMapping.getScore()+1.0:scoreMapping.getScore());
					scoreMapping.setScore(scoreMapping.getScore()>10?10:scoreMapping.getScore());
				}else{
					scoreMapping.setScore(scoreMapping.getScore()>0?scoreMapping.getScore()-0.25:scoreMapping.getScore());
					scoreMapping.setScore(scoreMapping.getScore()<0?0:scoreMapping.getScore());
				}
				visualRecognitionDaoImpl.updateScoreMapping(scoreMapping);
			}
		}else {
			for(Userproductscoremapping scoreMapping : userProductScoreMapping){
				if(scoreMapping.getScoremappingid().equals(auditTransaction.getUserproductscoremapping().getScoremappingid())){
					scoreMapping.setScore(scoreMapping.getScore()>0?scoreMapping.getScore()-1.0:scoreMapping.getScore());
					scoreMapping.setScore(scoreMapping.getScore()<0?0:scoreMapping.getScore());
				}else{
					scoreMapping.setScore(scoreMapping.getScore()<10?scoreMapping.getScore()+0.25:scoreMapping.getScore());
					scoreMapping.setScore(scoreMapping.getScore()>10?10:scoreMapping.getScore());
				}
				visualRecognitionDaoImpl.updateScoreMapping(scoreMapping);
			}
			ScoreMappingDetails scoreMappingDetails = visualRecognitionDaoImpl.getMaxScoreMappingDetails(auditTransaction.getVisualrecognisionuserprofiles().getUserprofileid());
			AdsMappingDetails adsMappingDetails = visualRecognitionDaoImpl.getAdsMapping(scoreMappingDetails.getUserProfileId(),scoreMappingDetails.getProductId());
			Visualrecognitionaudittransaction visualrecognitionaudittransaction = new Visualrecognitionaudittransaction();
			visualrecognitionaudittransaction.setAge(auditTransaction.getAge());
			visualrecognitionaudittransaction.setGender(auditTransaction.getGender());
			visualrecognitionaudittransaction.setUserproductadsmapping(new Userproductadsmapping());
			visualrecognitionaudittransaction.getUserproductadsmapping().setAdsmappingid(Long.parseLong(adsMappingDetails.getAdsMappingId().toString()));
			visualrecognitionaudittransaction.setCommercialproductsprofile(new Commercialproductsprofile());
			visualrecognitionaudittransaction.getCommercialproductsprofile().setProductid(Long.parseLong(adsMappingDetails.getProductId().toString()));
			visualrecognitionaudittransaction.setUserproductscoremapping(new Userproductscoremapping());
			visualrecognitionaudittransaction.getUserproductscoremapping().setScoremappingid(Long.parseLong(scoreMappingDetails.getScoreMappingId().toString()));
			visualrecognitionaudittransaction.setVisualrecognisionuserprofiles(new Visualrecognisionuserprofiles());
			visualrecognitionaudittransaction.getVisualrecognisionuserprofiles().setUserprofileid(Long.parseLong(adsMappingDetails.getUserProfileId().toString()));
			Long transactionCorrelation = visualRecognitionDaoImpl.insertTransaction(visualrecognitionaudittransaction);
			adFetchResponse = new VisualRecognitionAdFetchResponse();
			adFetchResponse.setAdUrl(adsMappingDetails.getAdsInfo());
			adFetchResponse.setCorrelationid(transactionCorrelation);
		}
		if(adFetchResponse==null){
			adFetchResponse = new VisualRecognitionAdFetchResponse();
			adFetchResponse.setStatus("success");
		}
		return adFetchResponse;
	}
	
	private String deriveEmotion(List<Double> postivies,List<Double> negatives,List<Double> neutrals){
		Double positiveValue = this.emotionFactorsSum(postivies);
		Double negativeValue = this.emotionFactorsSum(negatives);
		Double neutralValue = this.emotionFactorsSum(neutrals);
		String emotionResult = null;
		if(neutralValue > positiveValue && neutralValue > negativeValue){
			emotionResult=  "neutral";
		} else if(positiveValue > negativeValue){
			emotionResult= "positive";
		} else if(negativeValue > positiveValue){
			emotionResult= "negative";
		}
		return emotionResult;
	}
	
	private Double emotionFactorsSum(List<Double> list) {
	     Double sum = 0.0; 
	     for (Double i : list)
	         sum = sum + i;

	     return sum;
	}
	
	@Override
	public Object fetchEmotionAttributest(Map<String, Object> imageRequest) throws VisualRecognitionServiceException {
		String base64String = (String) imageRequest.get("image");
		Integer correlationid = (Integer) imageRequest.get("correlationid");
		Object responseJson = null;
		HttpClient httpclient = new DefaultHttpClient();
		BASE64Decoder decoder = new BASE64Decoder();
	    byte[] decodedBytes;
	    try {
				decodedBytes = decoder.decodeBuffer(base64String);
				System.out.println("Decoded upload data : " + decodedBytes.length);
				String uploadFile = "sample_" + this.getImageCount() + ".jpg";
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
				if (image == null) {
		           System.out.println("Buffered Image is null");
				}
				File f = new File(uploadFile);
				// write the image
				ImageIO.write(image, "jpg", f);
				URIBuilder builder = new URIBuilder(emotionsUriBase);
				// Prepare the URI for the REST API call.
		         URI uri = builder.build();
		         HttpPost request = new HttpPost(uri);

		         // Request headers.
		         //request.setHeader("Content-Type", "application/json");
		         request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
		         
		         File file = new File(uploadFile);
		         FileEntity reqEntity = new FileEntity(file, "application/octet-stream");
		         reqEntity.setContentType("application/octet-stream");
		         request.setEntity(reqEntity);
		         
		      // Execute the REST API call and get the response entity.
		         HttpResponse response = httpclient.execute(request);
		         HttpEntity entity = response.getEntity();
		         String jsonString = EntityUtils.toString(entity).trim();
		         ObjectMapper mapper = new ObjectMapper();
		         List<Map<String,Object>> emotionsList = mapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>(){});
		         Map<String,Object> emotionsMap = new HashMap<>();
		         emotionsMap.put("correlationid", correlationid);
		         emotionsMap.put("emotions", emotionsList);
		         String emotionsFaceURL = "http://localhost:7777/v1/apis/emotions/face";
		         
		         String emotionsFaceRequest = mapper.writeValueAsString(emotionsMap);
		         ResponseEntity<Object> advertisementFaceResponse = HttpUtil.postRequest(restTemplate, HttpMethod.POST, emotionsFaceURL, emotionsFaceRequest);
		         responseJson = (Object)advertisementFaceResponse.getBody();
		         
		         
	    } catch (IOException e) {
			LOG.error(e.getLocalizedMessage());
			throw new VisualRecognitionServiceException("API_SYS_01","Invalid Image","500");
		} catch (URISyntaxException e) {
			LOG.error(e.getLocalizedMessage());
			throw new VisualRecognitionServiceException("API_SYS_01","Invalid URI","500");
		}
	    return responseJson;
	}
	
	private synchronized int getImageCount(){
		return imageCount++;
	}

}
