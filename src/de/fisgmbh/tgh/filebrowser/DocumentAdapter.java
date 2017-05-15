package de.fisgmbh.tgh.filebrowser;

import javax.servlet.ServletException;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

public class DocumentAdapter extends ObjectAdapter {

	public DocumentAdapter(String id) {
		super(id);
	}
	
	public String getDocumentInfoAsJson() {
		StringBuilder builder = new StringBuilder();
		
		return builder.toString();
	}
	
	public static String getDocumentInfo(Document document) throws ServletException {
		StringBuilder builder = new StringBuilder();
		
		builder.append("{");
		builder.append("\"Name\": \"" + document.getName() + "\"");
		builder.append(",\"Id\": \"" + document.getId() + "\"");
		builder.append(",\"CreatedBy\": \"" + document.getCreatedBy() + "\"");
		builder.append(",\"Type\": \"Document\"");
		builder.append(",\"Filetype\": \"" + document.getContentStreamMimeType() + "\"");
		builder.append(",\"Filesize\": \"" + document.getContentStreamLength() + "\"");
		builder.append(",\"Last Modification\": \"" + document.getLastModificationDate().getTimeInMillis() + "\"");
		builder.append("}");
		
		return builder.toString();
	}
	
	public ContentStream getContentStream() throws ServletException {
		try {
			Session repository = ObjectAdapter.getSession();
			Document doc = (Document)repository.getObject(this.getId());
			return doc.getContentStream();
		} catch (ClassCastException e) {
			throw new ServletException("Error. You can only download documents.");
		}
	}
}
