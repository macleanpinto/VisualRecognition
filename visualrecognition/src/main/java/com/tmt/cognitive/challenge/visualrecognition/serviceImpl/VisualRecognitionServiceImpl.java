package com.tmt.cognitive.challenge.visualrecognition.serviceImpl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.tmt.cognitive.challenge.visualrecognition.dao.VisualRecognitionDaoImpl;
import com.tmt.cognitive.challenge.visualrecognition.entity.AdsMappingDetails;
import com.tmt.cognitive.challenge.visualrecognition.entity.ScoreMappingDetails;
import com.tmt.cognitive.challenge.visualrecognition.entity.VisualRecognitionAdFetchResponse;
import com.tmt.cognitive.challenge.visualrecognition.exception.VisualRecognitionServiceException;
import com.tmt.cognitive.challenge.visualrecognition.service.VisualRecognitionService;
import com.tmt.cognitive.challenge.visualrecognition.utility.HttpUtil;
import com.tmt.cognitive.challenge.visualrecognition.utility.VisualRecognitionServiceErrors;
import com.tmt.cognitive.challenge.visualrecognition.vo.Commercialproductsprofile;
import com.tmt.cognitive.challenge.visualrecognition.vo.Userproductadsmapping;
import com.tmt.cognitive.challenge.visualrecognition.vo.Userproductscoremapping;
import com.tmt.cognitive.challenge.visualrecognition.vo.Visualrecognisionuserprofiles;
import com.tmt.cognitive.challenge.visualrecognition.vo.Visualrecognitionaudittransaction;

import sun.misc.BASE64Decoder;

@Component
public class VisualRecognitionServiceImpl implements VisualRecognitionService {
	final static Logger LOG = Logger.getLogger(VisualRecognitionServiceImpl.class);

	
	@Value("${faceapi.subscriptionkey}")
	private String subscriptionKey;
	
	@Value("${faceapi.baseuri}")
	private String uriBase;
	
	
	private int imageCount = 0;
	@Autowired
	private VisualRecognitionDaoImpl visualRecognitionDaoImpl;

	@Autowired
	private VisualRecognitionServiceErrors visualRecognitionServiceErrors;

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public VisualRecognitionAdFetchResponse getAdUrlBasedOnAgeGender(List<Map<String,Object>> requestPayload)
			throws VisualRecognitionServiceException {
		VisualRecognitionAdFetchResponse visualRecognitionAdFetchResponse = new VisualRecognitionAdFetchResponse();
		Map<String,Object> requestMap = requestPayload.get(0);
		Map<String,Object> faceAttributes = (Map<String, Object>) requestMap.get("faceAttributes");
		Double age = (Double) faceAttributes.get("age");
		String gender = (String) faceAttributes.get("gender"); 
		ScoreMappingDetails scoreMappingDetails = visualRecognitionDaoImpl.getScoreMapping(age,gender.toLowerCase());
		AdsMappingDetails adsMappingDetails = visualRecognitionDaoImpl.getAdsMapping(scoreMappingDetails.getUserProfileId(),scoreMappingDetails.getProductId());
		System.out.println("Age : " + age + " and Gender " + gender);
		visualRecognitionAdFetchResponse.setAdUrl(adsMappingDetails.getAdsInfo());
		Visualrecognitionaudittransaction visualrecognitionaudittransaction = new Visualrecognitionaudittransaction();
		visualrecognitionaudittransaction.setAge(age);
		visualrecognitionaudittransaction.setGender(gender);
		visualrecognitionaudittransaction.setUserproductadsmapping(new Userproductadsmapping());
		visualrecognitionaudittransaction.getUserproductadsmapping().setAdsmappingid(Long.parseLong(adsMappingDetails.getAdsMappingId().toString()));
		visualrecognitionaudittransaction.setCommercialproductsprofile(new Commercialproductsprofile());
		visualrecognitionaudittransaction.getCommercialproductsprofile().setProductid(Long.parseLong(adsMappingDetails.getProductId().toString()));
		visualrecognitionaudittransaction.setUserproductscoremapping(new Userproductscoremapping());
		visualrecognitionaudittransaction.getUserproductscoremapping().setScoremappingid(Long.parseLong(scoreMappingDetails.getScoreMappingId().toString()));
		visualrecognitionaudittransaction.setVisualrecognisionuserprofiles(new Visualrecognisionuserprofiles());
		visualrecognitionaudittransaction.getVisualrecognisionuserprofiles().setUserprofileid(Long.parseLong(adsMappingDetails.getUserProfileId().toString()));
		Long transactionCorrelation = visualRecognitionDaoImpl.insertTransaction(visualrecognitionaudittransaction);
		visualRecognitionAdFetchResponse.setCorrelationid(transactionCorrelation);
		return visualRecognitionAdFetchResponse;
	}

	@Override
	public Object fetchFaceAttributes(Map<String, Object> imageRequest) throws VisualRecognitionServiceException {
		String base64String = (String) imageRequest.get("image");
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

				URIBuilder builder = new URIBuilder(uriBase);

		         // Request parameters. All of them are optional.
		         builder.setParameter("returnFaceId", "true");
		         builder.setParameter("returnFaceLandmarks", "false");
		         builder.setParameter("returnFaceAttributes", "age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise");

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
		         
		         // Request body.
		         /*StringEntity reqEntity = new StringEntity("{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/c/c3/RH_Louise_Lillian_Gish.jpg\"}");
		         request.setEntity(reqEntity);*/

		         // Execute the REST API call and get the response entity.
		         HttpResponse response = httpclient.execute(request);
		         HttpEntity entity = response.getEntity();
		         String jsonString = EntityUtils.toString(entity).trim();
		         
		         String advertisementFaceURL = "http://localhost:7777/v1/apis/advertisements/face";
		         
		         ResponseEntity<Object> advertisementFaceResponse = HttpUtil.postRequest(restTemplate, HttpMethod.POST, advertisementFaceURL, jsonString);
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
