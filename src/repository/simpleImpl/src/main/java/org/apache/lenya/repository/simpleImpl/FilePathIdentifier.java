package org.apache.lenya.repository.simpleImpl;

import org.apache.lenya.repository.api.Identifier;

public class FilePathIdentifier implements Identifier<String> {

	String idToken;
	
	@Override
	public void setIdentificationToken(String idToken) {
		this.idToken = idToken;
	}

	@Override
	public String getIdentificationToken() {
		return idToken;
	}

}
