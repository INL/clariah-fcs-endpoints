package org.ivdnt.fcs.endpoint.bls;

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

public class BlacklabServerQuery extends clariah.fcs.Query
{
	public static final String atHome = "http://localhost:8080/blacklab-server/";
	public static final String corpusAtHome="ezel";


	public static final String openSonarServer =  "http://opensonar.ato.inl.nl/blacklab-server/";
	public static final String openSonarCorpus="opensonar";

	public static final String defaultServer = openSonarServer;
	public static final String defaultCorpus = openSonarCorpus;

	String server = defaultServer;
	String corpus = defaultCorpus;

	String cqp = "[word='hadjememaar']";


	public int startPosition;
	public int maximumResults;
	public int totalNumberOfResults;

	public BlacklabServerQuery(String server, String corpus, String cqp)
	{
		super(server,corpus,cqp);
		this.server = server;
		this.corpus= corpus;
		this.cqp = cqp;
	}

	public String url()
	{
		try 
		{
			String url = server  +  "/" + corpus + "/"  + "hits?patt=" + URLEncoder.encode(cqp, "utf-8") + "&outputformat=json" +
					"&first=" + startPosition
					+ "&number=" + (maximumResults);
			return url;
		} catch (UnsupportedEncodingException e) 
		{
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

	public  List<Kwic> search() throws Exception
	{
		List<Kwic> results = new ArrayList<Kwic>();
		String url = this.url();

		System.err.println("URL to blacklab server: " + url);
		JSONObject response = fetch(url);

		// System.err.println("Response: " + response);

		JSONObject summary =  (JSONObject) response.get("summary");
		
		if (summary == null)
		{
			System.err.println("!Error: no summary in response " + response);
		}
		
		JSONArray hits = (JSONArray) response.get("hits");

		JSONObject docs = (JSONObject) response.get("docInfos");

		

		Object nof = summary.get("numberOfHits");

		if (nof instanceof Integer)
		{
			this.totalNumberOfResults = (Integer) nof;
		} else if (nof instanceof Long)
		{ 
			this.totalNumberOfResults = ((Long) nof).intValue();
		}


		for (int i = 0; i < hits.size(); i++) 
		{
			try
			{
				Kwic kwic = new Kwic(); results.add(kwic);
				JSONObject hit = (JSONObject) hits.get(i);
				
				// System.err.println("hit " + i + "=" + hit);
				

				JSONObject doc = (JSONObject) docs.get((String) hit.get("docPid"));
				Set<String> metadataProperties = doc.keySet();

				doc.forEach( (k,v) -> kwic.metadata.put(k.toString(), v.toString()) ); // or put this in separate document info objects?


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

				// System.err.println("Kwic: "  + i + ":" + kwic);
			 
			} catch (Exception e)
			{
				e.printStackTrace();
				throw e;
			}
		}

		System.err.printf("Loop completed, %d hits !\n", results.size());
		return results;
	}

	public BlacklabServerResultSet execute() throws Exception
	{
		List<Kwic> kwics = search();
		BlacklabServerResultSet bsrs = new BlacklabServerResultSet();
		bsrs.hits = kwics;
		bsrs.query = this;
		bsrs.totalNumberOfResults = this.totalNumberOfResults;

		System.err.println("execute OK: result set " + bsrs);
		return bsrs;
	}

}