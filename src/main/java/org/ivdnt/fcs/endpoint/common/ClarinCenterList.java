package org.ivdnt.fcs.endpoint.common;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ivdnt.util.FileUtils;

public class ClarinCenterList extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4249262880158005274L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		String xmlFileContent = null;

		// https://stackoverflow.com/questions/10621580/how-to-get-the-servlet-context-from-servletrequest-in-servlet-2-5
		ServletContext context = request.getSession().getServletContext();

		xmlFileContent = new FileUtils(context, "clarin_center_ivdnt.xml").readConfigFileAsString();

		// Set response content type
		response.setContentType("text/xml");

		// write the response
		try {

			PrintWriter out = response.getWriter();
			out.println(xmlFileContent);

		} catch (IOException e) {
			throw new RuntimeException("Exception while writing response.", e);
		}
	}

}
