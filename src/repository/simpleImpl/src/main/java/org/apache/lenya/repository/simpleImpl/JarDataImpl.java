package org.apache.lenya.repository.simpleImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.lenya.repository.api.Data;
import org.apache.lenya.repository.api.Identifier;


public class JarDataImpl implements Data{
	
	
	@Override
	public OutputStream getOutputStream(Identifier<?> idData) {
		//TODO : throw an error
		return null;
	}

	@Override
	public InputStream getInputStream(Identifier<?> idData) {
		//TODO : remove all this tests
		try {
			URL t = this.getClass().getResource(idData.getIdentificationToken().toString());
			System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDd");
			System.out.println(t);
			System.out.println(this.getClass().getResource("/repository/testFile.txt"));
			System.out.println(t.toURI().toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this.getClass().getResourceAsStream(idData.getIdentificationToken().toString());
	}
	
	

}
