package org.apache.lenya.repository.api;

public interface Identifier<T> {
	
	public void setIdentificationToken(T idToken);
	
	public T getIdentificationToken();

}
