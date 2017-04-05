package de.fisgmbh.tgh.filebrowser.documentservice;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

import com.sap.ecm.api.EcmService;
import com.sap.ecm.api.RepositoryOptions;
import com.sap.ecm.api.RepositoryOptions.Visibility;

public class ObjectAdapter {

	private static final String REPOSITORY_NAME = "de.fisgmbh.tgh.applman";
	private static final String REPOSITORY_SECRET = "de.fisgmbh.tgh.applman.tzfouhuw8kbqfgs7b1zvjku6fjk54lujsnty58d2p";
	
	private String id;
	
	public ObjectAdapter(String id) {
		this.id = id;
	}
	
	public void delete() {
		
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public static Session getSession() throws ServletException {
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
			// repository does not exist, so try to create it
			RepositoryOptions options = new RepositoryOptions();
			options.setUniqueName(uniqueName);
			options.setRepositoryKey(secretKey);
			options.setVisibility(Visibility.PROTECTED);
			ecmSvc.createRepository(options);
			// should be created now, so connect to it
			openCmisSession = ecmSvc.connect(uniqueName, secretKey);
		}
		return openCmisSession;
	}
	
}
