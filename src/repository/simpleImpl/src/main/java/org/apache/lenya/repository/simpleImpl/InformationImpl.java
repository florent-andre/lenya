package org.apache.lenya.repository.simpleImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ServiceLoader;

import org.apache.lenya.repository.api.Data;
import org.apache.lenya.repository.api.Identifier;
import org.apache.lenya.repository.api.Information;
import org.apache.lenya.repository.api.MetaData;
import org.apache.lenya.repository.api.Repository;
import org.apache.lenya.repository.exception.InformationException;

public class InformationImpl implements Information{

	private static ServiceLoader<Data> dataImplLoader = ServiceLoader.load(Data.class);
	
	private Identifier<String> id;
	private Data data = null;
	public InformationImpl(Identifier<String> id) throws InformationException{
		this.id = id;
		//this information can only deal with one data impl
		data = dataImplLoader.iterator().next();
	}
	@Override
	public InputStream getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream setMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getData() {
		return data.getInputStream(id);
	}

	@Override
	public OutputStream setData() {
		// TODO Auto-generated method stub
		return null;
	}

}
