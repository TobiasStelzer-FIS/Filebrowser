package de.fisgmbh.tgh.filebrowser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

public class FolderAdapter extends ObjectAdapter {

	public FolderAdapter(String id) {
		super(id);
	}
		
	public FolderAdapter createfolder(String name) throws ServletException {
		Session repository = ObjectAdapter.getSession();
		CmisObject o = repository.getObject(this.getId());
		
		try {
			Folder parentFolder = (Folder)o;
			
			if (this.containsCmisObject(name)) {
				throw new ServletException("Error. A Folder with this name already exists");
			}
			
			Map<String, String> newFolderProps = new HashMap<String, String>();
			newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			newFolderProps.put(PropertyIds.NAME, name);
			
			Folder f = parentFolder.createFolder(newFolderProps);
			
			return new FolderAdapter(f.getId());
			
		} catch (ClassCastException e) {
			throw new ServletException("Error. You have to provide a valid ID of a Folder");
		}
	}
	
	public DocumentAdapter createdocument(String name, InputStream content) throws ServletException {
		// createdocument
		Session repository = ObjectAdapter.getSession();
	
		try {
			Folder parentFolder = (Folder)repository.getObject(this.getId());
			
			ContentStream contentStream = repository.getObjectFactory().createContentStream(name, -1, "application/octet-stream", content);
			
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			properties.put(PropertyIds.NAME, name);
			Document newDocument = parentFolder.createDocument(properties, contentStream, VersioningState.NONE);
			
			return new DocumentAdapter(newDocument.getId());
		} catch (ClassCastException e) {
			throw new ServletException("Error. You have to provide a valid ID of a Folder");
		}
	}

	public String getNavigationInfo() throws ServletException {
		StringBuilder builder = new StringBuilder();
		Session repository = ObjectAdapter.getSession();
		
		try {
			Folder folder = (Folder)repository.getObject(this.getId());
			builder.append("{");
			builder.append("\"SelectedFolder\": ");
			
			builder.append(FolderAdapter.getFolderInfo(folder, true));
			
			builder.append("}");
		} catch (ClassCastException e) {
			throw new ServletException("Error. You can only navigate to folders");
		}
		
		return builder.toString();
	}
	
	public static String getFolderInfo(Folder folder, boolean includeNavigationData) throws ServletException{
		StringBuilder builder = new StringBuilder();
		
		builder.append("{");
		builder.append("\"Name\": \"" + folder.getName() + "\"");
		builder.append(",\"Id\": \"" + folder.getId() + "\"");
		builder.append(",\"CreatedBy\": \"" + folder.getCreatedBy() + "\"");
		builder.append(",\"Type\": \"Folder\"");
		
		if (includeNavigationData) {
			builder.append(",\"Parents\": " + getParentData(folder));
			builder.append(",\"Children\": " + getChildrenData(folder));
		}
		
		builder.append("}");
		
		return builder.toString();
	}
	
	public static String getParentData(Folder folder) throws ServletException {
		StringBuilder builder = new StringBuilder();
		
		List<Folder> parents = new ArrayList<Folder>();
		Folder parent = folder.getFolderParent();
		while (parent != null) {
			parents.add(parent);
			parent = parent.getFolderParent();
		}
		// Parents-List is in wrong order (e.g. "Level2 / Level1 / root")
		Collections.reverse(parents);
		
		builder.append("[");
		for (int i = 0; i < parents.size(); i++) {
			if (i != 0)
				builder.append(",");
			
			builder.append(getFolderInfo(parents.get(i), false));
		}
		builder.append("]");
		
		return builder.toString();
	}
	
	public static String getChildrenData(Folder folder) throws ServletException {
		StringBuilder builder = new StringBuilder();
		
		boolean isFirst = true;
		builder.append("[");
		for (CmisObject child : folder.getChildren()) {
			if (!isFirst) 
				builder.append(",");
			
			try {
				Folder f = (Folder)child;
				builder.append(FolderAdapter.getFolderInfo(f, false));
			} catch (ClassCastException e) {
				Document d = (Document)child;
				builder.append(DocumentAdapter.getDocumentInfo(d));
			}
			
			isFirst = false;
		}
		builder.append("]");
		
		return builder.toString();
	}
	
	private String recursiveHierarchy(Folder folder, Session repository) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("{");
		builder.append("\"Name\": \"" + folder.getName() + "\"");
		builder.append(",\"Id\": \"" + folder.getId() + "\"");
		builder.append(",\"CreatedBy\": \"" + folder.getCreatedBy() + "\"");
		builder.append(",\"Type\": \"Folder\"");
		builder.append(",\"Children\": [");
		
		List<Folder> subfolders = filterChildrenByFolder(folder);
		for (int i = 0; i < subfolders.size(); i++) {
			Folder f = subfolders.get(i);
			if (i > 0) {
				builder.append(",");
			}
			builder.append(recursiveHierarchy(f, repository));
		}
		
		builder.append("]");
		builder.append("}");
		
		return builder.toString();
	}
	
	public String getHierarchy() throws ServletException {
		StringBuilder builder = new StringBuilder();
		Session repository = ObjectAdapter.getSession();
		
		try {
			Folder folder = (Folder)repository.getObject(this.getId());
			builder.append("{");
			builder.append("\"Hierarchy\": ");
			builder.append("{");
			builder.append("\"root\":");
			
			builder.append(recursiveHierarchy(folder, repository));
			
			builder.append("}");
			builder.append("}");
		} catch (ClassCastException e) {
			throw new ServletException("Error. You can only navigate to folders");
		}
		
		return builder.toString();
	}
	
	private List<Folder> filterChildrenByFolder(Folder folder) {
		List<Folder> list = new ArrayList<Folder>();
		
		for (CmisObject o : folder.getChildren()) {
			try {
				Folder f = (Folder)o;
				list.add(f);
			} catch (ClassCastException e) {
				// do nothing
			}
		}
		
		return list;
	}
	
	private boolean containsCmisObject(String name) throws ServletException {
		Session repository = ObjectAdapter.getSession();
		CmisObject o = repository.getObject(this.getId());
		
		try {
			Folder parentFolder = (Folder)o;
			
			for (CmisObject child : parentFolder.getChildren()) {
				if (child.getName().equals(name)) {
					return true;
				}
			}
			return false;
			
		} catch (ClassCastException e) {
			throw new ServletException("Error. You have to provide a valid ID of a Folder");
		}
	}
	
	public static FolderAdapter getRootAdapter() throws ServletException {
		String rootId;
		
		Session repository = ObjectAdapter.getSession();
		rootId = repository.getRootFolder().getId();
		
		return new FolderAdapter(rootId);
	}
}
