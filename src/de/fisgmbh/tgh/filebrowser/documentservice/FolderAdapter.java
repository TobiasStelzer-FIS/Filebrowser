package de.fisgmbh.tgh.filebrowser.documentservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

public class FolderAdapter extends ObjectAdapter {

	public FolderAdapter(String id) {
		super(id);
	}
	
	public String getHierarchyWithDocuments() throws ServletException {
		StringBuilder infoBuilder = new StringBuilder();
		Session repository = ObjectAdapter.getSession();
		
		try {
			Folder folder = (Folder)repository.getObject(this.getId());
			infoBuilder.append("{");
			infoBuilder.append("\"Hierarchy\": ");
			
			getFolderInfoAsJson(folder, repository, infoBuilder, false, -1, -1, true);
			
			infoBuilder.append("}");
		} catch (ClassCastException e) {
			throw new ServletException("Error. You can only navigate to folders");
		}
		
		return infoBuilder.toString();
	}
	
	public String getFolderInfoAsJson() throws ServletException {
		StringBuilder infoBuilder = new StringBuilder();
		Session repository = ObjectAdapter.getSession();
		
		try {
			Folder folder = (Folder)repository.getObject(this.getId());
			infoBuilder.append("{");
			infoBuilder.append("\"SelectedFolder\": ");
			
			getFolderInfoAsJson(folder, repository, infoBuilder, false, 0, 1, true);
			
			infoBuilder.append("}");
		} catch (ClassCastException e) {
			throw new ServletException("Error. You can only navigate to folders");
		}
		
		return infoBuilder.toString();
	}
	
	private void getFolderInfoAsJson(Folder folder, Session repository, StringBuilder builder, boolean hasNext, int depth, int maxDepth, boolean includeDocuments) throws ServletException {
		ItemIterable<CmisObject> children = folder.getChildren();
		Iterator<CmisObject> it = children.iterator();
		
		builder.append("{");
		builder.append("\"Name\": \"" + folder.getName() + "\",");
		builder.append("\"Id\": \"" + folder.getId() + "\",");
		builder.append("\"Type\": \"Folder\",");
		//builder.append("\"ItemCount\": \"" + children.getTotalNumItems() + "\",");
		
		if (depth == -1 || maxDepth == -1 || depth < maxDepth) {
			builder.append("\"Children\": [");
			
			while (it.hasNext()) {
				CmisObject o = it.next();
				try {
					Folder f = (Folder)o;
					getFolderInfoAsJson(f, repository, builder, it.hasNext(), depth+1, maxDepth, includeDocuments);
				} catch (ClassCastException e) {
					try {
						Document d = (Document)o;
						getDocumentInfoAsJson(d, builder, it.hasNext());
					} catch (ClassCastException e2) {
						System.err.println("Error. File with ID " + o.getId() + " is neither a Folder nor a Document");
					}
				}
			}
		}
		builder.append("]");
		builder.append("}");
		if (hasNext) {
			builder.append(",");
		}
	}
	
	private void getDocumentInfoAsJson(Document document, StringBuilder builder, boolean hasNext) throws ServletException {
		builder.append("{");
		builder.append("\"Name\": \"" + document.getName() + "\",");
		builder.append("\"Id\": \"" + document.getId() + "\",");
		builder.append("\"Type\": \"Document\",");
		builder.append("\"Filetype\": \"" + document.getContentStreamMimeType() + "\",");
		builder.append("\"Filesize\": \"" + document.getContentStreamLength() + "\"");
		builder.append("}");
		if (hasNext) {
			builder.append(",");
		}		
	}
	
	public void delete() {
		// delete
	}
	
	public FolderAdapter createfolder(String name) {
		FolderAdapter fa = null;
		// createfolder
		// if (alreadyExists)
		//		return false;
		return fa;
	}
	
	public DocumentAdapter createdocument(String name, InputStream content) {
		// createdocument
		Session rep = null;
		Folder parentFolder = (Folder)rep.getObject(this.getId());
		
		ContentStream contentStream = rep.getObjectFactory().createContentStream(name, -1, "application/octet-stream", content);
		Document newDocument = parentFolder.createDocument(new HashMap<String, Object>(), contentStream, VersioningState.NONE);
				
		return new DocumentAdapter(newDocument.getId());
	}
	
	public String getHierarchyAsJson() {
		StringBuilder builder = new StringBuilder();
		// get json
		
		return builder.toString();
	}
	
	public static FolderAdapter getRootAdapter() throws ServletException {
		String rootId;
		
		Session repository = ObjectAdapter.getSession();
		rootId = repository.getRootFolder().getId();
		
		return new FolderAdapter(rootId);
	}
}
