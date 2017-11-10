package org.ivdnt.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;

public class FileUtils {
	
	public FileUtils() {
		
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
	
	
	public String readConfigFile(ServletContext context, String filename) throws IOException{
		
		String filepath = context.getContextPath() + File.separatorChar+ filename;		
		
		StringBuilder sb = new StringBuilder();
		
		try{
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
		}
		catch (Exception e){
			throw new RuntimeException("Error while reading the "+filename+" configuration file", e);
		}
		
		return sb.toString();
	}

}
