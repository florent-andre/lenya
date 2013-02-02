package org.apache.lenya.repository.simpleImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.lenya.repository.api.Data;
import org.apache.lenya.repository.api.Identifier;


public class FSDataImpl implements Data{
	
	private static String FSproperty = "repositoryPath";
	private String fsPath;
	
	public FSDataImpl() throws IOException{
		Properties prop = new Properties();
		InputStream in = getClass().getResourceAsStream("/dataFSpath.properties");
		prop.load(in);
		fsPath = prop.getProperty(FSproperty);
		in.close();
	}
	
	@Override
	public OutputStream getOutputStream(Identifier<?> idData) {
		//TODO : throw an error
		return null;
	}

	@Override
	public InputStream getInputStream(Identifier<?> idData) {
		//TODO : use a data mapper instead of that
		String dataPath = fsPath+idData.getIdentificationToken().toString();
		InputStream result = null;
		//todo remove this tests
		try {
			System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDd");
			System.out.println(dataPath);
			result = new FileInputStream(dataPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	

}
