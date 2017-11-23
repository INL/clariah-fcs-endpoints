package org.ivdnt.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Several methods to read file content
 * 
 * @author fannee
 *
 */
public class FileUtils {
	
	
	// -------------------------------------------------------------
	// constructor
	
	public FileUtils() {
		
	}
	
	// -------------------------------------------------------------
	// write to files
	
	
	public void writeStringToFile(String filepath, String content)
	{

		FileOutputStream fos;
		OutputStreamWriter out;
		
		try {
			fos = new FileOutputStream(filepath);
			out = new OutputStreamWriter(fos, "UTF-8");
			
			out.write(content);
			
			out.close();
			fos.close();
			
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}
	
	
	// -------------------------------------------------------------
	// read files
	
	
	// get string from file (moved from nederlab.stuff.IO.java)
	
	public String readStringFromFile(String fileName)
	{
		try
		{
		  FileReader r  = new FileReader(new File(fileName));
		  BufferedReader b = new BufferedReader(r);
		  String l;
		  StringBuffer sb = new StringBuffer();
		  
		  while ((l = b.readLine()) != null)
		  {
			  sb.append(l);
			  sb.append("\n");
		  }
		  b.close();
		  return sb.toString();
		  
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	// get list from file (moved from util.StringUtils.java)
	
	public List<String> readListFromFile(String fileName) throws IOException
	{
		String l;
		BufferedReader b = new BufferedReader(new FileReader(fileName));

		List<String> L = new ArrayList<String>();
		while ((l = b.readLine()) != null)
		{
			L.add(l);
		}
		b.close();
		return L;
	}
	
	
	
	// -------------------------------------------------------------
	// get file from resource folder in different formats:
	// as string or as file
	
	public String getResourceAsString(String resourcePath) {

		StringBuffer result = new StringBuffer("");

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(resourcePath).getFile());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	 }
	
	
	// https://stackoverflow.com/questions/14089146/file-loading-by-getclass-getresource
	public File getResourceAsFile(String resourcePath) {
	    try {
	        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
	        if (in == null) {
	            return null;
	        }

	        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
	        tempFile.deleteOnExit();

	        try (FileOutputStream out = new FileOutputStream(tempFile)) {
	            //copy stream
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = in.read(buffer)) != -1) {
	                out.write(buffer, 0, bytesRead);
	            }
	        }
	        
	        return tempFile;
	    } catch (IOException e) {
	    	
	    	throw new RuntimeException("Error while reading resource "+resourcePath, e);
	    }
	}
	
	
	// -------------------------------------------------------------
	// get file out of the -config subfolder in the Webapps folder 
	
	public String readConfigFile(ServletContext context, String filename) throws IOException{
		
		String contextpath = context.getRealPath(filename);
		
		String filepath = contextpath.replace("blacklab-sru-server", "blacklab-sru-server-config");
		
		// debug
		if (false)
		{
			System.err.println(contextpath);
			System.err.println(filepath);
		}		
		
		StringBuilder sb = new StringBuilder();
		
		
		FileInputStream fstream = new FileInputStream(filepath);
		
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String strLine;
		while ((strLine = br.readLine()) != null) {				
			sb.append(strLine);
			sb.append("\n");
		}
		br.close();
		in.close();
		
		
		return sb.toString();
	}
	
	public Document readConfigDoc(ServletContext context, String filename) throws IOException, ParserConfigurationException, SAXException{
		
		String contextpath = context.getRealPath(filename);
		
		String filepath = contextpath.replace("blacklab-sru-server", "blacklab-sru-server-config");
		
		
		// debug
		if (false)
		{
			System.err.println(contextpath);
			System.err.println(filepath);
		}		
		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);
		   
		DocumentBuilder db;
		Document doc;
		
		FileInputStream fstream = new FileInputStream(filepath);
		
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		
		db = dbf.newDocumentBuilder();
		doc = db.parse(in);
		
		in.close();
		
		return doc;
	}

}
