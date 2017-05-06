package com.skholr;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("updateS3bucket")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     * @throws SQLException 
     * @throws TikaException 
     * @throws SAXException 
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() throws ClassNotFoundException, IOException, SAXException, TikaException, SQLException {
    	boolean flag = ActionFacade.addToS3();
    	if(flag)
    		return "Got it!";
    	else
    		return "Balls !";
    }
}
