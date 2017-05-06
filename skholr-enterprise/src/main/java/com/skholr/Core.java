package com.skholr;
/*
 * Formatting methods,parsers and other utilities
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Core {
	public Core() {
	}

	public static String parsePDFtoString(InputStream pdfDocument) throws IOException, SAXException, TikaException {

		BodyContentHandler handler = new BodyContentHandler(-1);
		FileInputStream inputStream = getFileStreamFromInputStream(pdfDocument);
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		// parsing the document using PDF parser
		PDFParser pdfparser = new PDFParser();
		pdfparser.parse(inputStream, handler, metadata, pcontext);

		// getting the content of the document
		return handler.toString();
	}

	public static FileInputStream getFileStreamFromInputStream(InputStream inputStream) throws IOException {

		File placeHolder = File.createTempFile("file", "pdf");
		FileInputStream returnStream = null;
		placeHolder.deleteOnExit();

		try (FileOutputStream out = new FileOutputStream(placeHolder)) {
			IOUtils.copy(inputStream, out);
			returnStream = new FileInputStream(placeHolder);
		}

		return returnStream;
	}

	public static String printMap(Map<String, String> inputMap) {

		String returnString = "";
		
		for (String key : inputMap.keySet()) {
			
			returnString = inputMap.get(key); 
		}
		return returnString;
	}

	public static void cleanFiles(List<File> inputList) {
		// TODO Auto-generated method stub
		for(File input : inputList){
			
			input.delete();
		}		
	}
}
