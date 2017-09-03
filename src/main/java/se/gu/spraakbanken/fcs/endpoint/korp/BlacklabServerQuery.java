package se.gu.spraakbanken.fcs.endpoint.korp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import clariah.fcs.*;

public class BlacklabServerQuery 
{
	public static final String defaultServer = "http://localhost:8080/blacklab-server-1.6.0/";
	String server = defaultServer;
	
	String cqp = "[word='paard']";
	String corpus = "ezel";
	
	int startPosition;
	int maximumResults;
	
	public BlacklabServerQuery(String server, String corpus, String cqp)
	{
		this.server = server;
		this.corpus= corpus;
		this.cqp = cqp;
	}
	
	public String url()
	{
		try {
			String url = server  +  "/" + corpus + "/"  + "hits?patt=" + URLEncoder.encode(cqp, "utf-8") + "&outputformat=json" +
		"&first=" + startPosition
		+ "&number=" + (maximumResults);
			return url;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	static List<String> tokenProperty(JSONObject context, String pname)
	{
		List<String> l = new ArrayList<String>();
		// System.err.println("look for: " + pname + " in  " + context);
		JSONArray words = (JSONArray) context.get(pname);
		for (int i = 0; i < words.size(); i++) {
			l.add((String) words.get(i));
		}
		return  l;
	}
	
	public static JSONObject fetch(String url) throws Exception 
	{
		// Read from the specified URL.
		InputStream is = new URL(url).openStream();
		JSONParser parser = new JSONParser();
		try {
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder b = new StringBuilder();
			while ((line = br.readLine()) != null) {
				b.append(line);
			}
			JSONObject x = new JSONObject();
			Object o =  parser.parse(b.toString());
			return  (JSONObject) o;
		} finally {
			is.close();
		}
	}
	
	public static List<Kwic> search(BlacklabServerQuery query) throws Exception
	{
		List<Kwic> results = new ArrayList<Kwic>();
		String url = query.url();
		
		System.err.println("URL to blacklab server: " + url);
		JSONObject response = fetch(url);
		//System.err.println("Response: " + response);
	
		JSONArray hits = (JSONArray) response.get("hits");
		
		JSONObject docs = (JSONObject) response.get("docInfos");

		
		for (int i = 0; i < hits.size(); i++) 
		{
			try
			{
			Kwic kwic = new Kwic(); results.add(kwic);
			JSONObject hit = (JSONObject) hits.get(i);
			// System.err.println("hit " + i + "=" + hit);
			// Add the document title and the hit information

			JSONObject doc = (JSONObject) docs.get((String) hit.get("docPid"));

			JSONObject leftContext = (JSONObject) hit.get("left");
			JSONObject match = (JSONObject) hit.get("match");
			JSONObject rightContext = (JSONObject) hit.get("right");

			Set<String> tokenProperties =  match.keySet();

			int hitStart = 0;
			int hitEnd=0;

			for (String pname: tokenProperties)
			{
				kwic.tokenPropertyNames.add(pname);
				List<String> list = tokenProperty(leftContext, pname);

				if (pname.equals("word")) hitStart = list.size();
				List<String> matches = tokenProperty(match, pname);
				list.addAll(matches);
				if (pname.equals("word")) hitEnd = list.size();
				list.addAll(tokenProperty(rightContext, pname));
				kwic.tokenProperties.put(pname, list);
			}

			kwic.hitStart = hitStart;
			kwic.hitEnd = hitEnd;

			System.err.println("Kwic: "  + i + ":" + kwic);
			// Context of the hit is passed in arrays, per property
			// (word/lemma/PoS). Right now we only want to display the 
			// words. This is how we join the word array to a string.    
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return results;
	}

	public BlacklabServerResultSet execute() throws Exception
	{
		List<Kwic> kwics = search(this);
		BlacklabServerResultSet bsrs = new BlacklabServerResultSet();
		bsrs.hits = kwics;
		bsrs.query = this;
		//bsrs.startPosition = 
		return bsrs;
	}
	
}