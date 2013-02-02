package org.apache.lenya.repository.api;

import java.io.InputStream;
import java.io.OutputStream;

public interface Data {
	
	//TODO : check if the in/outStream interface fit well with every possibilities
	// or use generics ??
	
	 /**
     * @return The output stream to write the document content to.
     */
    public OutputStream getOutputStream(Identifier<?> idData);
    
    /**
     * @return The input stream to obtain the document's content.
     */
    public InputStream getInputStream(Identifier<?> idData);
    
}
