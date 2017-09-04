package org.ivdnt.fcs.endpoint.nederlab;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * 
 * @author does
 * Zie ook https://github.com/meertensinstituut/mtas/blob/master/conf/solr/schemaOeaw.xml 
 */

public class NederlabClient 
{
	static String url = "http://www.nederlab.nl/testbroker/search/";

	com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
	public int contextSize = 8;

	public String queryNederlab(Object o)
	{
		try {
			String s = mapper.writeValueAsString(o);
			return queryNederlab(s);
		} catch (JsonProcessingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void testJSONQuery(String query)
	{
		String result = queryNederlab(query);
		parseResults(result);
	}

	public void testCQLQuery(String CQL)
	{
		HitIterator hi = new HitIterator(this,CQL);
		while (hi.hasNext())
		{
			System.out.println(hi.next());
		}
	}

	public Stream<Hit> getHits(String CQL)
	{
		Iterator<Hit> hits = new HitIterator(this,CQL);
		Iterable<Hit> iterable = () -> hits;
		Stream<Hit> targetStream = StreamSupport.stream(iterable.spliterator(), false);
		return targetStream;
	}

	public void queryCQLFromFile(String fileName)
	{
		String cql = IO.readStringFromFile(fileName).trim();
		testCQLQuery(cql);
	}

	public List<Hit> getResults(String CQL, int start, int number)
	{
		Map<String, String> r = new HashMap<>();
		r.put("_START_", new Integer(start).toString());
		r.put("_NUMBER_", new Integer(number).toString());
		r.put("_CONTEXT_", new Integer(contextSize).toString());
		String cx = CQL.replaceAll("\"", "\\\\\\\\" + "\"");
		//System.err.println(cx);
		r.put("_QUERY_", cx);
		String q = (new QueryTemplate()).expandTemplate(r); // silly to reInit
		String results = queryNederlab(q);
		return parseResults(results);
	}

	public List<Hit> parseResults(String result)
	{
		//System.out.println(result);
		DocumentContext context = JsonPath.parse(result);
		List<Hit> results = new ArrayList<>();
		try
		{

			//Object x = context.read("$[*].[kwic].[*].[list]");

			JSONArray documents = (JSONArray) context.read("$['documents']");

			List<Document> docs = new ArrayList<Document>();
			Map<String,Document> docMap = new HashMap<>();
			for (Object d: documents)
			{
				String docJ = mapper.writeValueAsString(d); // ugly reserialization!!!
				Document doc = mapper.readValue(docJ, Document.class);
				docs.add(doc);
				//System.err.println(docJ);
			}

			for (Document d: docs)
			{
				docMap.put(d.NLCore_NLIdentification_nederlabID, d);
			}

			if (false) return results;
			Object x = context.read("$[*]['kwic'][*]['list']");

			//System.err.println("return type:"  + x.getClass());
			if (x instanceof net.minidev.json.JSONArray)
			{
				JSONArray a = (JSONArray) x;
				//System.err.println("Array of " + a.size());
				for (int i=0; i < a.size(); i++)
				{
					JSONArray listOfDocumentsWithHits = (JSONArray) a.get(i);

					for (int j=0; j < listOfDocumentsWithHits.size(); j++)
					{
						Map documentWithListOfHits = (Map) listOfDocumentsWithHits.get(j);
						String documentKey = (String) documentWithListOfHits.get("documentKey");

						Object list = documentWithListOfHits.get("list");
						JSONArray hitsInDoc = (JSONArray) list;
						for (int k=0; k < hitsInDoc.size(); k++)
						{
							Object hit = hitsInDoc.get(k);

							List<TokenProperty> tokenProps = new ArrayList<>();
							Map hitMap = (Map) hit;
							JSONArray tokens = (JSONArray) hitMap.get("tokens");

							for (int l=0; l < tokens.size(); l++)
							{
								// en nu wel mappen met jackson ??
								Map tok = (Map) (tokens.get(l));
								String tokJ = mapper.writeValueAsString(tok); // ugly reserialization!!!
								TokenProperty t = mapper.readValue(tokJ, TokenProperty.class);
								tokenProps.add(t);
							}
							Hit h = new Hit(tokenProps);
							h.documentKey = documentKey;
							h.document = docMap.get(documentKey);
							h.startPosition = (Integer) hitMap.get("startPosition");
							h.endPosition = (Integer) hitMap.get("endPosition");
							int l=0;

							// bleuh

							for (Token t: h.tokens)
							{
								//System.err.println(t.startPosition + " " + t);
								if (t.contentToken && t.startPosition==h.startPosition)
								{
									int newStartPosition = l;
									h.endPosition = l + (h.endPosition - h.startPosition);
									h.startPosition = l;
									break;
								}
								if (t.contentToken)
									l++;
							}
							results.add(h);
							//System.err.println(h);
						}
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			System.err.println(context);
		}
		return results;
	}

	public String queryNederlab(String query)
	{
		try
		{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			//add request header
			con.setRequestMethod("POST");
			//con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");



			// Send post request
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os,"UTF-8");
			osw.write(query);

			osw.flush();
			osw.close();

			int responseCode = con.getResponseCode();

			System.err.println("Query : " + query);
			System.err.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			return response.toString();
		} catch (Exception e)
		{

			e.printStackTrace();
			return null;
		}	
	}

	Map<String,Object> readQueryAsObject(String fileName)
	{
		try {
			Map<String,Object> userData =  (Map<String,Object>) mapper.readValue(new File(fileName), Map.class);
			String x = mapper.writeValueAsString(userData);
			System.err.println(x);
			return userData;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	String readQuery(String fileName)
	{
		try {
			Map<String,Object> userData =  (Map<String,Object>) mapper.readValue(new File(fileName), Map.class);
			String x = mapper.writeValueAsString(userData);
			System.err.println(x);
			return mapper.writeValueAsString(userData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args)
	{
		NederlabClient n = new NederlabClient();
		n.queryCQLFromFile(args[0]);
	}
}
