package de.fisgmbh.tgh.filebrowser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.chemistry.opencmis.commons.data.ContentStream;

/*
@WebServlet(
	name="ServletFilebrowser",
	urlPatterns={"/filebrowser/*"}
)
@MultipartConfig(
	location="/tmp", 
	fileSizeThreshold=1024*1024, 
	maxFileSize=1024*1024*5, 
	maxRequestSize=1024*1024*5*5
)
*/
public class ServletFilebrowser extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public ServletFilebrowser() {
		super();
		
//		ObjectAdapter.setRepositoryName(getServletConfig().getInitParameter("REPOSITORY_NAME"));
//		ObjectAdapter.setRepositorySecret(getServletConfig().getInitParameter("REPOSITORY_SECRET"));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getPathInfo();
		if (path == null)
			path = "";
		String[] pathElements = path.split("/");
		List<String> paramNames = Collections.list(request.getParameterNames());

		if (pathElements.length == 2 && paramNames.size() == 0) {
			// Pfad ist angegeben, keine Parameter sind angegeben
			download(pathElements[1], response);

		} else if (paramNames.size() != 0 && pathElements.length < 2) {
			// Parameter sind angegeben, kein Pfad ist angegeben
			handleAction(request, response, request.getParameter("action"));

		} else {
			// Pfad UND Parameter sind angegeben oder
			// weder Pfad noch Parameter sind angegeben
			// oder Pfad hat ung�ltige Anzahl von Elementen
			throw new ServletException("Error. Invalid URL. You have to provide a path OR parameters.");
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Part partAdditionalData = request.getPart("fileUploader-data");
		InputStream inAdditionalData = partAdditionalData.getInputStream();
		InputStreamReader isrAdditionalData = new InputStreamReader(inAdditionalData);
		BufferedReader brAdditionalData = new BufferedReader(isrAdditionalData);
		String parameterLine = brAdditionalData.readLine();

		String[] parameters = parameterLine.split(";");
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < parameters.length; i++) {
			String[] paramKeyValue = parameters[i].split(":");
			map.put(paramKeyValue[0], paramKeyValue[1]);
		}

		String action = map.get("action");

		switch (action) {
		case "upload":
			String parentid = map.get("parentid");
			String name = map.get("name");
			Part partFileContent = request.getPart("fileUploader");
			upload(parentid, name, partFileContent.getInputStream(), response);
			break;
		default:
			throw new ServletException("Error. Invalid value for 'action'-Parameter");
		}
	}

	private void upload(String parentid, String filename, InputStream fileContent, HttpServletResponse response)
			throws ServletException {
		FolderAdapter fa = new FolderAdapter(parentid);
		DocumentAdapter newDocument = fa.createdocument(filename, fileContent);
		if (newDocument != null) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			throw new ServletException("Error. Probably a document with this name already exists");
		}
	}

	private void download(String documentId, HttpServletResponse response) throws ServletException {
		InputStream input = null;
		OutputStream output = null;

		try {
			DocumentAdapter da = new DocumentAdapter(documentId);
			ContentStream content = da.getContentStream();
			if (content == null) {
				throw new ServletException("Error. Couldn't receive the files data");
			}
			input = content.getStream();
			output = response.getOutputStream();

			IOUtils.writeToOutputStream(input, output);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException e) {
			throw new ServletException("Error when trying to send the response");
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				throw new ServletException("Error. Couldn't close file");
			}
		}
	}

	private void handleAction(HttpServletRequest request, HttpServletResponse response, String action)
			throws ServletException {

		switch (action) {
		case "navigate":
			String id = request.getParameter("id");

			FolderAdapter fa = null;
			if (id == null || id.equals("")) {
				fa = FolderAdapter.getRootAdapter();
			} else {
				fa = new FolderAdapter(id);
			}

			String jsonResponse = fa.getNavigationInfo();
			try {
				response.getOutputStream().print(jsonResponse);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (IOException e) {
				throw new ServletException("Error when trying to send the response");
			}
			break;

		case "delete":
			id = request.getParameter("id");
			ObjectAdapter oa = new ObjectAdapter(id);
			oa.delete();
			response.setStatus(HttpServletResponse.SC_OK);
			break;

		case "rename":
			id = request.getParameter("id");
			String newName = request.getParameter("name");
			oa = new ObjectAdapter(id);
			oa.rename(newName);
			response.setStatus(HttpServletResponse.SC_OK);
			break;
			
		case "createfolder":
			String parentid = request.getParameter("parentid");
			String name = request.getParameter("name");
			fa = new FolderAdapter(parentid);
			FolderAdapter newFolder = fa.createfolder(name);
			if (newFolder != null) {
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				throw new ServletException("Error. Probably a folder with this name already exists");
			}
			break;

		case "hierarchy":
			try {
				fa = FolderAdapter.getRootAdapter();
				jsonResponse = fa.getHierarchy();
				response.getOutputStream().print(jsonResponse);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (IOException e) {
				throw new ServletException("Error when trying to send the response");
			}
			break;

		default:
			throw new ServletException("Error. Invalid value for 'action'-Parameter");
		}
	}

}
