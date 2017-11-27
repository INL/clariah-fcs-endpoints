package org.ivdnt.fcs.endpoint.common;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ivdnt.util.FileUtils;
import org.ivdnt.util.Utils;

public class ClarinCenterList extends HttpServlet {
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		String xmlFileContent = null;
		
		// https://stackoverflow.com/questions/10621580/how-to-get-the-servlet-context-from-servletrequest-in-servlet-2-5
		ServletContext context = request.getSession().getServletContext();
		
		// read the config file from the -config subfolder
		try {
			xmlFileContent = new FileUtils().readConfigFile(context, "clarin_center_ivdnt.xml");
		} catch (IOException e) {
			Utils.printStackTrace(e);
		}
		
		// Set response content type
		response.setContentType("text/xml");
	
		// write the response
		try {
			PrintWriter out = response.getWriter();
			out.println(xmlFileContent);
		} catch (IOException e) {
			Utils.printStackTrace(e);
		}
	}
	
}
