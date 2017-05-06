package com.skholr;
/*
 * TODO:
 * 1) Code bucketFetch population
 * 2) Use hadoop connection to insert into EMR environment using hive
 * 3) Create method to do map reduce using hive to fetch word count
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.sql.ResultSet;

import org.joda.time.DateTime;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/*
 * DataBroker negotiates with connection layer to create data methods to contact 
 * cloudfront's s3 and elastic search
 */
public class DataBroker {

	public static List<String> newKeysList = new ArrayList();
	
	//uses connection factory to connect to s3 and fetch pdf files uploaded since last fetch
	public static Map<String,InputStream> fetchNewDocuments() throws IOException, ClassNotFoundException, SQLException{
		
		if(newKeysList.isEmpty()){
			populalteNewKeyList();
		}
		
		//This will be replaced by day's contents
		String bucketName = "skholrpdfdocs";
		String key = "";
		
		Map<String, InputStream> returnMap = new HashMap();
		
		for(String keyName : newKeysList){
			
			key = keyName.split("\\.")[0];
			S3Object object = ConnectionFactory.getS3Client().getObject(new GetObjectRequest(bucketName, keyName));
			InputStream oData = object.getObjectContent();
			returnMap.put(key, oData);
		}
				
		return returnMap;
	}
	
	//connects to RDS to find out fetch key list.
	public static void populalteNewKeyList() throws ClassNotFoundException, SQLException{

		//String bucket = "skholrpdfdocs";
		String queryText = "SELECT * FROM DocsRepo WHERE Processed<>'1'";
		Statement st = ConnectionFactory.getRDSConnection().createStatement();
		ResultSet res = st.executeQuery(queryText);
		
		while(res.next()){
			
			newKeysList.add(res.getString("FileName"));
		}
	}
	
	public static void setProcessedFlag() throws ClassNotFoundException, SQLException{
		
		String queryText = "UPDATE DocsRepo SET Processed = 1";
		Statement st = ConnectionFactory.getRDSConnection().createStatement();
		st.executeUpdate(queryText);
	}
	
	public static boolean insertIntoS3(Map<String,String> inputMap) throws IOException{
		
		String bucketName = "skholrpdfdocs/parsedoutput";
		String keyName = ""; String fileName = "";
		File uploadFile = null;
		FileWriter fileWriter = null;
		Map<String,File> keyFileMap = new HashMap();
		List<File> cleanList = new ArrayList();
		
		for(String key: inputMap.keySet()){
			
			String content = inputMap.get(key);
			uploadFile = new File(key + ".txt");
			fileWriter = new FileWriter(uploadFile);
			fileWriter.write(content);
			fileWriter.flush();
			fileWriter.close();
			keyFileMap.put(key+".txt", uploadFile);
		}
		
		for(String key: keyFileMap.keySet()){
			
			ConnectionFactory.getS3Client().putObject(new PutObjectRequest(bucketName, key, keyFileMap.get(key)));
			cleanList.add(keyFileMap.get(key));
		}
		
		Core.cleanFiles(cleanList);
		return true;
	}

	public static void closeUsedConnections() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		if(ConnectionFactory.getRDSConnection()!=null && (!ConnectionFactory.getRDSConnection().isClosed())){
			ConnectionFactory.getRDSConnection().close();
		}
	}
}
