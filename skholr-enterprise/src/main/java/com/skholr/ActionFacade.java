package com.skholr;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class ActionFacade {

	public static Map<String,String> getParsedContentMap() throws IOException, SAXException, TikaException, ClassNotFoundException, SQLException{
		
		InputStream oData;
		String pContent;
		Map<String, InputStream> newKeyDocumentsMap = DataBroker.fetchNewDocuments();
		Map<String, String> newKeyContentMap = new HashMap<String,String>();
		
		for(String key: newKeyDocumentsMap.keySet()){
			
			oData = newKeyDocumentsMap.get(key);
			pContent = Core.parsePDFtoString(oData);
			
			newKeyContentMap.put(key, pContent);
		}
		
		return newKeyContentMap;
	}
	
	public static boolean addToS3() throws IOException, SAXException, TikaException, ClassNotFoundException, SQLException{
		
		Map<String,String> parsedContentMap = getParsedContentMap();
		boolean returnflag = DataBroker.insertIntoS3(parsedContentMap);
		DataBroker.setProcessedFlag();
		DataBroker.closeUsedConnections();
		return returnflag;
	}
	
	public static void main(String[] args) throws IOException, SAXException, TikaException, ClassNotFoundException, SQLException{
		
		System.out.println("TESTING PARSER");
		Map<String, String> parsedContent = getParsedContentMap();
		//System.out.println(Core.printMap(parsedContent));
		DataBroker.insertIntoS3(parsedContent);
		DataBroker.setProcessedFlag();
		DataBroker.closeUsedConnections();
	}
}
