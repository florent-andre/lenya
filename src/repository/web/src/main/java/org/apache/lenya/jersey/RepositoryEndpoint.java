package org.apache.lenya.jersey;

import java.io.IOException;
import java.util.ServiceLoader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.lenya.repository.api.Data;
import org.apache.lenya.repository.api.Identifier;
import org.apache.lenya.repository.api.Information;
import org.apache.lenya.repository.api.Repository;
import org.apache.lenya.repository.exception.RepositoryException;
import org.apache.lenya.repository.simpleImpl.FilePathIdentifier;
import org.apache.lenya.repository.simpleImpl.RepositoryImpl;

@Path("/test")
public class RepositoryEndpoint {
	
	private static ServiceLoader<Repository> repositoriesLoader = ServiceLoader.load(Repository.class);
	
	public RepositoryEndpoint(){
		
	}
	
	// This method is called if TEXT_PLAIN is request
	  @GET
	  @Produces(MediaType.TEXT_PLAIN)
	  public String sayPlainTextHello() {
	    return "Hello Jersey";
	  }

	  // This method is called if XML is request
	  @GET
	  @Produces(MediaType.TEXT_XML)
	  public String sayXMLHello() {
	    return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
	  }

	  // This method is called if HTML is request
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() throws IOException, RepositoryException {
		 Identifier<String> identifier = new FilePathIdentifier();
		 identifier.setIdentificationToken("testFile.txt");
		 
		 //for now only the jar one is available
		 //TODO : use the repository jar
		 Repository repo = repositoriesLoader.iterator().next();
		 
		 String content = IOUtils.toString(repo.retrieve(identifier).getData());
		 
	    return "<html> " + "<title>" + "Hello Jersey" + "</title>"
	        + "<body><h1>" + content + "</body></h1>" + "</html> ";
	  }
}
