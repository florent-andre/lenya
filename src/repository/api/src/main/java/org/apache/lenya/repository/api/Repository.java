package org.apache.lenya.repository.api;

import org.apache.lenya.repository.exception.RepositoryException;

public interface Repository {

	//TODO : change document to information here
	public Information retrieve(Identifier<?> id) throws RepositoryException;

	public Identifier<?> store(Information information) throws RepositoryException;

	public Information createNewRevision() throws RepositoryException;

	public Boolean delete() throws RepositoryException;
}
