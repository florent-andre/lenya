package org.apache.lenya.repository.simpleImpl;

import java.util.ServiceLoader;

import org.apache.lenya.repository.api.Identifier;
import org.apache.lenya.repository.api.Information;
import org.apache.lenya.repository.api.Repository;
import org.apache.lenya.repository.exception.InformationException;
import org.apache.lenya.repository.exception.RepositoryException;

public class RepositoryImpl implements Repository{

	public RepositoryImpl(){
	};

	@Override
	public Identifier<?> store(Information information) throws RepositoryException {
		throw new RepositoryException("Not implemented, can't store in a jar");
	}
	
	@Override
	public Information retrieve(Identifier<?> id) throws RepositoryException {
		String path = id.getIdentificationToken().toString();
		//TODO : use a pathMapper instead of that
		//create a new identifier related to the repository
		Identifier<String> repoIdentifier = new FilePathIdentifier();
		repoIdentifier.setIdentificationToken("/repository/"+path);
		Information information;
		try{
			information = new InformationImpl(repoIdentifier);
			//TODO : remove this line juste for test
			information.getData();
			//end test
		}catch(InformationException e){
			throw new RepositoryException();
		}
		
		return information;
	}

	@Override
	public Boolean delete() throws RepositoryException {
		throw new RepositoryException("Not implemented, can't delete in a jar");
	}
	
	@Override
	public Information createNewRevision() throws RepositoryException {
		throw new RepositoryException("Not implemented, can't store in a jar");
	}
}
