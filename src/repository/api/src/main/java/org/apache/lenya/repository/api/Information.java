package org.apache.lenya.repository.api;

import java.io.InputStream;
import java.io.OutputStream;

public interface Information {
	
	//an information *can* have metadata
	//an information have data.
	
//	public MetaData getMetadata();
//	public void setMetadata(MetaData md);
//	
//	public Data getData();
//	public void setData(Data d);
	
	public InputStream getMetadata();
	public OutputStream setMetadata();
	
	public InputStream getData();
	public OutputStream setData();

}
