package de.fisgmbh.tgh.filebrowser;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

import com.sap.ecm.api.EcmService;
import com.sap.ecm.api.RepositoryOptions;
import com.sap.ecm.api.RepositoryOptions.Visibility;

public class ObjectAdapter {

	private static String REPOSITORY_NAME = "de.fisgmbh.tgh.applman";
	private static String REPOSITORY_SECRET = "de.fisgmbh.tgh.applman.tzfouhuw8kbqfgs7b1zvjku6fjk54lujsnty58d2p";
	
	private static Session repository = null;
	
	private String id;
	
	public ObjectAdapter(String id) {
		this.id = id;
	}
	
	public void delete() throws ServletException {
		Session repository = ObjectAdapter.getSession();
		CmisObject obj;
		
		try {
			obj = repository.getObject(this.getId());
		} catch (CmisObjectNotFoundException e) {
			throw new ServletException("Error. The Object you want to delete does not exist.");
		}
		
		try {
			Folder f = (Folder)obj;
			f.deleteTree(true, UnfileObject.DELETE, true);
		} catch (ClassCastException e1) {
			try {
				Document d = (Document)obj;
				d.deleteAllVersions();
			} catch (ClassCastException e2) {
				throw new ServletException("Error. CmisObject can't be deleted. It's neither a Folder nor a Document");
			}
		}
	}
	
	public void rename(String newName) throws ServletException {
		Session repository = ObjectAdapter.getSession();
		
		try {
			CmisObject obj = repository.getObject(this.getId());
			obj.rename(newName);
		} catch (CmisObjectNotFoundException e) {
			throw new ServletException("Error. The Object you want to rename does not exist.");
		}
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public static Session getSession() throws ServletException {
		if (repository == null) {
			repository = createSession();
		}
		return repository;
	}
	
	private static Session createSession() throws ServletException {
		// Use a unique name with package semantics
		String uniqueName = REPOSITORY_NAME;
		// Use a secret key only known to your application (min. 10 chars)
		String secretKey = REPOSITORY_SECRET;
		Session openCmisSession = null;
		EcmService ecmSvc = null;
		try {
			InitialContext ctx = new InitialContext();
			String lookupName = "java:comp/env/" + "EcmService";
			ecmSvc = (EcmService) ctx.lookup(lookupName);
		} catch (NamingException e) {
			throw new ServletException(e.getMessage());
		}		
		try {
			// connect to my repository
			openCmisSession = ecmSvc.connect(uniqueName, secretKey);
		} catch (CmisObjectNotFoundException e) {
			throw new ServletException("Couldn't connect to the repository."
					+ " Maybe the credentials are wrong or the repository doesn't exist or the service is currently not available.");
			/*
			// repository does not exist, so try to create it
			RepositoryOptions options = new RepositoryOptions();
			options.setUniqueName(uniqueName);
			options.setRepositoryKey(secretKey);
			options.setVisibility(Visibility.PROTECTED);
			ecmSvc.createRepository(options);
			// should be created now, so connect to it
			openCmisSession = ecmSvc.connect(uniqueName, secretKey);
			*/
		}
		return openCmisSession;
	}
	
	public static void setRepositoryName(String repositoryName) {
		REPOSITORY_NAME = repositoryName;
	}
	
	public static void setRepositorySecret(String repositorySecret) {
		REPOSITORY_SECRET = repositorySecret;
	}
	
}
