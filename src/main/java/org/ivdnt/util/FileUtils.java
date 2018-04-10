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
import java.net.URL;
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

	private String filepath;
	private String newFilepath;
	ServletContext context;
	DataInputStream in;

	// -------------------------------------------------------------
	// constructor

	public FileUtils(String filepath) {

		this.filepath = filepath;
	}

	public FileUtils(ServletContext context, String filepath) throws FileNotFoundException {

		this.context = context;
		this.filepath = filepath;
		// translate path into the path to the config file
		String contextpath = this.context.getRealPath(this.filepath);
		this.newFilepath = contextpath.replace("blacklab-sru-server", "blacklab-sru-server-config");
		FileInputStream fstream = new FileInputStream(this.newFilepath);
		this.in = new DataInputStream(fstream);
		
	}

	// -------------------------------------------------------------
	// write to files

	public void writeStringToFile(String content) {

		FileOutputStream fos;
		OutputStreamWriter out;

		try {
			fos = new FileOutputStream(this.filepath);
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

	public String readStringFromFile() {
		try {
			FileReader r = new FileReader(new File(this.filepath));
			BufferedReader b = new BufferedReader(r);
			String l;
			StringBuilder sb = new StringBuilder();

			while ((l = b.readLine()) != null) {
				sb.append(l);
				sb.append("\n");
			}
			b.close();
			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// get list from file (moved from util.StringUtils.java)

	public List<String> readListFromFile() throws IOException {
		String l;
		BufferedReader b = new BufferedReader(new FileReader(this.filepath));

		List<String> L = new ArrayList<String>();
		while ((l = b.readLine()) != null) {
			L.add(l);
		}
		b.close();
		return L;
	}

	// -------------------------------------------------------------
	// get file from resource folder in different formats:
	// as string or as file

	public String getResourceAsString() {
		StringBuilder result = new StringBuilder("");

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(this.filepath);
		String file2 = resource.getFile();
		File file = new File(file2);

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
	public File getResourceAsFile() {
		try {
			InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(this.filepath);
			if (in == null) {
				return null;
			}

			File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
			tempFile.deleteOnExit();

			try (FileOutputStream out = new FileOutputStream(tempFile)) {
				// copy stream
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}

			return tempFile;
		} catch (IOException e) {

			throw new RuntimeException("Error while reading resource " + this.filepath, e);
		}
	}

	// -------------------------------------------------------------
	// get file out of the -config subfolder in the Webapps folder

	// fews versions with different output: URL, String or Document

	public String readConfigFileAsString() throws IOException {
		// build string output
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		StringBuilder sb = new StringBuilder();
		String strLine;
		while ((strLine = br.readLine()) != null) {
			sb.append(strLine);
			sb.append("\n");
		}
		br.close();
		in.close();

		return sb.toString();
	}


	public URL readConfigFileAsURL() throws IOException {
		// return path as URL
		// https://stackoverflow.com/questions/6098472/pass-a-local-file-in-to-url-in-java
		File file = new File(this.newFilepath);
		if (!file.exists())
			throw new FileNotFoundException("File not found in blacklab-sru-server-config/");
		return file.toURI().toURL();
	}

	public Document readConfigFileAsDoc() throws IOException, ParserConfigurationException, SAXException {
		// build the document
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);

		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(this.in);

		this.in.close();

		return doc;
	}

}
