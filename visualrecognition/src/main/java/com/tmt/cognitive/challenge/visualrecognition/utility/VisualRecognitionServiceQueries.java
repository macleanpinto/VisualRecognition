package com.tmt.cognitive.challenge.visualrecognition.utility;

public class VisualRecognitionServiceQueries {

	public static String SCOREMAPPING = "select SCOREMAPPINGID,USERPROFILEID,PRODUCTID,SCORE from userproductscoremapping where userprofileid = (select userprofileid from visualrecognisionuserprofiles where minimumage >= :age and GENDER=:gender order by USERPROFILEID ASC LIMIT 1) ORDER BY SCORE DESC LIMIT 1";
	public static String ADSMAPPING = "SELECT ADSMAPPINGID,USERPROFILEID,PRODUCTID,ADSINFO FROM userproductadsmapping WHERE USERPROFILEID  = :userprofileid and PRODUCTID = :productid";
	public static String UPDATETRANSACTION = "UPDATE visualrecognitionaudittransaction SET EMOTIONCHECKRESULT = :result WHERE TRANSACTIONID = :transactionid";
	public static String MAXSCOREMAPPING = "select SCOREMAPPINGID,USERPROFILEID,PRODUCTID,SCORE from userproductscoremapping where userprofileid = :userprofileid ORDER BY SCORE DESC LIMIT 1";
	
}
