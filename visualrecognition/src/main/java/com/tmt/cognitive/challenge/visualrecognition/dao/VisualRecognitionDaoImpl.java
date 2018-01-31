package com.tmt.cognitive.challenge.visualrecognition.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmt.cognitive.challenge.visualrecognition.entity.AdsMappingDetails;
import com.tmt.cognitive.challenge.visualrecognition.entity.ScoreMappingDetails;
import com.tmt.cognitive.challenge.visualrecognition.utility.VisualRecognitionServiceQueries;
import com.tmt.cognitive.challenge.visualrecognition.vo.Userproductscoremapping;
import com.tmt.cognitive.challenge.visualrecognition.vo.Visualrecognisionuserprofiles;
import com.tmt.cognitive.challenge.visualrecognition.vo.Visualrecognitionaudittransaction;

@Service
@Transactional
public class VisualRecognitionDaoImpl implements VisualRecognitionDao {

	private final Logger logger = LoggerFactory.getLogger(VisualRecognitionDaoImpl.class);

	@Autowired
	private SessionFactory _sessionFactory;

	private Session getSession() {
		return _sessionFactory.getCurrentSession();
	}

	@Override
	public ScoreMappingDetails getScoreMapping(Double age, String gender) {
		logger.info("Getting the score mapping details");
		ScoreMappingDetails scoreMappingDetails = null;
		try {
			Query query = getSession().createSQLQuery(VisualRecognitionServiceQueries.SCOREMAPPING);
			query.setParameter("age", age);
			query.setParameter("gender", gender);
			List<Object[]> results = query.list();
			if(results!=null && !results.isEmpty()){
				Object[] result = results.get(0);
				scoreMappingDetails = new ScoreMappingDetails();
				scoreMappingDetails.setScoreMappingId(Integer.valueOf(result[0].toString()));
				scoreMappingDetails.setUserProfileId(Integer.valueOf(result[1].toString()));
				scoreMappingDetails.setProductId(Integer.valueOf(result[2].toString()));
				scoreMappingDetails.setScore(Double.valueOf(result[3].toString()));
			}
		} catch(Exception ex){
			
		}
		return scoreMappingDetails;
	}
	
	public ScoreMappingDetails getMaxScoreMappingDetails(Long userProfileId){
		logger.info("Getting the score mapping details");
		ScoreMappingDetails scoreMappingDetails = null;
		try {
			Query query = getSession().createSQLQuery(VisualRecognitionServiceQueries.MAXSCOREMAPPING);
			query.setParameter("userprofileid", userProfileId);
			List<Object[]> results = query.list();
			if(results!=null && !results.isEmpty()){
				Object[] result = results.get(0);
				scoreMappingDetails = new ScoreMappingDetails();
				scoreMappingDetails.setScoreMappingId(Integer.valueOf(result[0].toString()));
				scoreMappingDetails.setUserProfileId(Integer.valueOf(result[1].toString()));
				scoreMappingDetails.setProductId(Integer.valueOf(result[2].toString()));
				scoreMappingDetails.setScore(Double.valueOf(result[3].toString()));
			}
		} catch(Exception ex){
			
		}
		return scoreMappingDetails;
	}

	@Override
	public AdsMappingDetails getAdsMapping(Integer userProfileId, Integer productId) {
		logger.info("Getting the score mapping details");
		AdsMappingDetails adsMappingDetails = null;
		try {
			Query query = getSession().createSQLQuery(VisualRecognitionServiceQueries.ADSMAPPING);
			query.setParameter("userprofileid", userProfileId);
			query.setParameter("productid", productId);
			List<Object[]> results = query.list();
			if(results!=null && !results.isEmpty()){
				Object[] result = results.get(0);
				adsMappingDetails = new AdsMappingDetails();
				adsMappingDetails.setAdsMappingId(Integer.valueOf(result[0].toString()));
				adsMappingDetails.setUserProfileId(Integer.valueOf(result[1].toString()));
				adsMappingDetails.setProductId(Integer.valueOf(result[2].toString()));
				adsMappingDetails.setAdsInfo(result[3].toString());
			}
		} catch(Exception ex){
			
		}
		return adsMappingDetails;
	}

	@Override
	public Long insertTransaction(Visualrecognitionaudittransaction visualrecognitionaudittransaction) {
		logger.info("persisting the audit transaction object");
		try {
			getSession().save(visualrecognitionaudittransaction);
		} catch (Exception e) {
			return null;
		}
		return visualrecognitionaudittransaction.getTransactionid();
	}

	@Override
	public boolean updateTransaction(String result, Long transactionid) {
		try {
			Query query = getSession().createSQLQuery(VisualRecognitionServiceQueries.UPDATETRANSACTION);
			query.setParameter("result", result);
			query.setParameter("transactionid", transactionid);
			query.executeUpdate();
		} catch(Exception ex){
			return false;
		}
		return true;
	}

	@Override
	public Visualrecognitionaudittransaction getTransaction(Long transactionid){
		logger.info("fetching the audit transaction based on transaction id" + transactionid);
		Visualrecognitionaudittransaction auditTransaction = null;
		try {
			Criteria crit = getSession().createCriteria(Visualrecognitionaudittransaction.class);
			crit.add(Restrictions.eq("transactionid", transactionid));
			List<Visualrecognitionaudittransaction> auditTransactionList = crit.list();
			if(auditTransactionList!=null && !auditTransactionList.isEmpty()){
				auditTransaction = auditTransactionList.get(0);
			}
		} catch (Exception e) {
			logger.error("error while fetching details" + e);
		}
		return auditTransaction;
	}
	
	@Override
	public List<Userproductscoremapping> getScoreMappings(Long userProfileId) {
		logger.info("fetching the score mapping for user profile id" + userProfileId);
		List<Userproductscoremapping> scoreMappings = null;
		try {
			Criteria crit = getSession().createCriteria(Userproductscoremapping.class,"userproductscoremapping");
			crit.createAlias("userproductscoremapping.visualrecognisionuserprofiles", "visualrecognisionuserprofiles");
			crit.add(Restrictions.eq("visualrecognisionuserprofiles.userprofileid", userProfileId));
			scoreMappings = crit.list();
		} catch (Exception e) {
			logger.error("error while fetching details" + e);
		}
		return scoreMappings;
	}
	
	
	@Override
	public void updateScoreMapping(Userproductscoremapping userproductscoremapping) {
		logger.info("persisting the audit transaction object");
		try {
			getSession().update(userproductscoremapping);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
	}
}
