package org.ivdnt.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Several methods to read file content
 * 
 * @author fannee, peter
 *
 */
public class FileUtils {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/** Path in clariah-fcs-endpoints-config dir (inside webapps dir) */
	private final String userFilePath;

	/** Path inside .war file (i.e. /WEB-INF/...) */
	private final String insideWarFilePath;

	/** For getting resources from .war file */
	ServletContext context;

	/** For parsing XML */
	DocumentBuilderFactory dbf;

	// -------------------------------------------------------------
	// constructor

	public FileUtils(ServletContext context, String filePath) {
		this.context = context;
        // translate path into the path to the config file
		String contextpath = this.context.getRealPath(filePath);
		this.userFilePath = contextpath.replace("clariah-fcs-endpoints", "clariah-fcs-endpoints-config");
		this.insideWarFilePath = File.separator + "WEB-INF" + File.separator + filePath;

		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);
	}

	// -------------------------------------------------------------
	// read files

	public Document parseXml() {
		try (InputStream in = getInputStream()) {
			try {
                return dbf.newDocumentBuilder().parse(in);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				throw new RuntimeException("Not able to convert stream to doc: " + in, e);
			}
		} catch (IOException e) {
			throw new RuntimeException("Not able to convert stream to doc: " + getURL(), e);
		}
	}

	// General method, which tries to read config file from user config directory,
	// then defaults to WEB-INF directory packaged with war
	// Returns InputStream
    public InputStream getInputStream() {
		InputStream in;
		URL url;
		try {
			// Try user-defined config file in clariah-fcs-endpoints-config directory
			in = new DataInputStream(new FileInputStream(userFilePath));
			logger.info("[config] File read from config dir: " + this.userFilePath);
		} catch (FileNotFoundException e) {
			// If that fails, read config file from WEB-INF directory in war
			logger.info("[config] Reading from config dir failed: " + this.userFilePath);
			try {
				url = context.getResource(insideWarFilePath);
				if (url == null) {
					throw new FileNotFoundException();
				}
				in = url.openStream();
			} catch (IOException e1) {
				throw new RuntimeException("[config] Reading from war file failed: " + this.insideWarFilePath, e1);
			}
			logger.info("[config] File read from war file: " + this.insideWarFilePath);
		}
		return in;
	}

	public String readToString() {
		try (InputStream stream = getInputStream()) {
			StringBuilder result = new StringBuilder("");
			Scanner scanner = new Scanner(stream);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();
			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException("Not able to convert stream to string: " + getURL(), e);
		}
	}

	public URL getURL() {
		// return path as URL
		// https://stackoverflow.com/questions/6098472/pass-a-local-file-in-to-url-in-java
		URL url = null;
		File file = new File(userFilePath);
		if (file.exists()) {
			// Try user-defined config file in clariah-fcs-endpoints-config directory
			try {
				url = file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException("[config] Reading URL from config dir failed: " + userFilePath, e);
			}
			logger.info("[config] URL read from config dir: " + this.userFilePath);
		} else {
			// If that fails, read config file from WEB-INF directory in war
			try {
				url = context.getResource(insideWarFilePath);
				if (url == null) {
					throw new FileNotFoundException();
				}
			} catch (IOException e) {
				throw new RuntimeException("[config] Reading from war file failed: " + this.insideWarFilePath, e);
			}
			logger.info("[config] File read from war file: " + this.insideWarFilePath);
		}
		return url;
	}

	/**
	 * Check if the file was found in either the user config directory or the
	 * war file.
	 *
	 * @return true if the file was found, false otherwise
	 */
	public boolean wasFound() {
		File file = new File(userFilePath);
        try {
            return file.exists() || context.getResource(insideWarFilePath) != null;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
