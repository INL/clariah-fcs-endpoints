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
	ServletContext context;

	// -------------------------------------------------------------
	// constructor

	public FileUtils(String filepath) {

		this.filepath = filepath;
	}

	public FileUtils(ServletContext context, String filepath) {

		this.context = context;
		this.filepath = filepath;
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
			StringBuffer sb = new StringBuffer();

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
		StringBuffer result = new StringBuffer("");

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

		// translate path into the path to the config file
		String contextpath = this.context.getRealPath(this.filepath);
		String newFilepath = contextpath.replace("blacklab-sru-server", "blacklab-sru-server-config");

		// build string output

		StringBuilder sb = new StringBuilder();
		FileInputStream fstream = new FileInputStream(newFilepath);

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

	public URL readConfigFileAsURL() throws IOException {

		// translate path into the path to the config file

		String contextpath = this.context.getRealPath(this.filepath);
		String newFilepath = contextpath.replace("blacklab-sru-server", "blacklab-sru-server-config");

		// return path as URL
		// https://stackoverflow.com/questions/6098472/pass-a-local-file-in-to-url-in-java
		File file = new File(newFilepath);
		if (!file.exists())
			throw new FileNotFoundException("File not found in blacklab-sru-server-config/");
		return file.toURI().toURL();
	}

	public Document readConfigFileAsDoc() throws IOException, ParserConfigurationException, SAXException {

		// translate path into the path to the config file

		String contextpath = this.context.getRealPath(this.filepath);
		String newFilepath = contextpath.replace("blacklab-sru-server", "blacklab-sru-server-config");

		// build the document

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);

		DocumentBuilder db;
		Document doc;

		FileInputStream fstream = new FileInputStream(newFilepath);

		DataInputStream in = new DataInputStream(fstream);

		db = dbf.newDocumentBuilder();
		doc = db.parse(in);

		in.close();

		return doc;
	}

}
