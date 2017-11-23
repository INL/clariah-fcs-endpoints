package org.ivdnt.fcs.endpoint.blacklab;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ivdnt.util.JsonUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clariah.fcs.results.Document;
import clariah.fcs.results.Kwic;
import clariah.fcs.results.ResultSet;

/**
 * This class is about sending a query to the BlacklabServer
 * and collect its response
 * 
 * @author jesse 
 *
 */
public class BlacklabServerQuery extends clariah.fcs.client.Query
{	
	
	private String server = BlacklabConstants.defaultServer;
	private String corpus = BlacklabConstants.defaultCorpus;	
	private String cqp = 	BlacklabConstants.cqp;


	// ------------------------------------------------------------------------------
	
	/**
	 * BlacklabServerQuery constructor
	 * 
	 * @param server, a URL string
	 * 
	 * @param corpus, a corpus name, like 'opensonar' (those are declared
	 *                as Resource pid's in WEB-INF/endpoint-description.xml)
	 *                
	 * @param cqp, a query like [word='lopen']
	 * 
	 */
	public BlacklabServerQuery(String server, String corpus, String cqp)
	{
		super(server, corpus, cqp);
		this.server = server;
		this.corpus= corpus;
		this.cqp = cqp;
	}
	
	
	// ------------------------------------------------------------------------------

	/**
	 * Build a SRU request URL 
	 * to be able to send some CQL query.
	 * 
	 * The CQL query will be put into the 'patt' parameter of the SRU request. 
	 * 
	 * @return the SRU URL with the CQL in it 
	 */
	public String getSruRequestUrl()
	{
		try 
		{
			String url = server  +  "/" + corpus + "/"  + "hits?" +
					"patt=" + URLEncoder.encode(cqp, "utf-8") + 
					"&outputformat=json" +
					"&first=" + this.getStartPosition() +
					"&number=" + this.getMaximumResults();
			
			return url;
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * Execute a prepared search (prepared in BlacklabServerEndpointSearchEngine.search)
	 * and put the results into a blacklabServerResultSet
	 */
	public ResultSet execute() throws Exception
	{
		// search
		
		ResultSet resultSet = new ResultSet();		
		List<Kwic> kwics = this.search(resultSet);
		
		// build a resultSet
		
		resultSet.setHits( kwics );
		resultSet.setQuery( this );
		resultSet.setTotalNumberOfResults( this.getTotalNumberOfResults() );

		System.err.println("execute OK: result set " + resultSet);
		return resultSet;
	}
	
	
	
	// ---------------------------------------------------------------------------------------
	
	// private methods, which are sub-routines of the execute() method
	

	/**
	 * Send a query to Blacklab and get the response
	 * 
	 * @param blacklabServerResultSet
	 * @return List of keywords in context (Kwic)
	 * @throws Exception
	 */
	private List<Kwic> search(ResultSet blacklabServerResultSet) throws Exception
	{
		
		String blackLabSruUrl = this.getSruRequestUrl();
		
		System.err.println("URL to blacklab server: " + blackLabSruUrl);
		
		
		// send a query to Blacklab and get the response
		
		JSONObject jsonObjResponse = this.sendQuery(blackLabSruUrl);
		
		
		// parse the response
		
		JSONObject summary =  	(JSONObject) jsonObjResponse.get("summary");
		JSONArray hits = 		(JSONArray) jsonObjResponse.get("hits");
		JSONObject docs = 		(JSONObject) jsonObjResponse.get("docInfos");
		
		if (summary == null)
		{
			System.err.println("!Error: no summary in response " + jsonObjResponse);
		}
		
		docs.keySet().forEach(
				docId ->
				{
					Document d = new Document();
					JSONObject doc = (JSONObject) docs.get(docId);
					doc.forEach( 
							(k,v) -> d.addMetadata( k.toString(), v.toString() ) 
					);
					
					blacklabServerResultSet.addDocument(docId.toString(), d);
				}
				);	
		
		
		// set number of hits
		
		Object nof = summary.get("numberOfHits");

		if (nof instanceof Integer)
		{
			this.setTotalNumberOfResults( (Integer) nof );
		} 
		else if (nof instanceof Long)
		{ 
			this.setTotalNumberOfResults( ((Long) nof).intValue() );
		}

		
		// process the hits:
		//
		// for each hit, we will build a list of token
		// consisting of the match and its left and  right context

		List<Kwic> results = parseResults(hits, docs);		
		

		System.err.printf("Loop completed, %d hits !\n", results.size());
		return results;
	}
	
	
	
	/**
	 * Parse the hits and documents
	 * returned by Blacklab
	 * 
	 * @param hits
	 * @param docs
	 * @return
	 */
	private List<Kwic> parseResults(JSONArray hits, JSONObject docs){	
		
		JsonUtils jsonUtils = new JsonUtils();
		List<Kwic> results = new ArrayList<Kwic>();
		
		for (int i = 0; i < hits.size(); i++) 
		{
			try
			{
				Kwic kwic = new Kwic(); 
				results.add(kwic);
				
				JSONObject hit = (JSONObject) hits.get(i);
				JSONObject doc = (JSONObject) docs.get((String) hit.get("docPid"));

				doc.forEach( (k,v) -> kwic.getMetadata().put(k.toString(), v.toString()) ); 
				// or put this in separate document info objects?


				// the results consist of 3 distinct parts:
				// the match, its context to the left, and its context to the right
				
				JSONObject leftContext =	(JSONObject) hit.get("left");
				JSONObject match = 			(JSONObject) hit.get("match");
				JSONObject rightContext =	(JSONObject) hit.get("right");

				// initialize details about matched token
				
				Set<String> matchedTokenProperties =  match.keySet();
				int hitStart = 0;
				int hitEnd = 0;

				// now build tokens list
				// with [1] left context, [2] match, and [3] right context 
				
				for (String pname : matchedTokenProperties)
				{
					List<String> tokensList = new ArrayList<String>();
					
					// [1] -----------------------------
					
					// add LEFT context
					tokensList.addAll( jsonUtils.getProperty(leftContext, pname) );

					// [2] -----------------------------
					
					// add MATCH
					// and note the start and end position of it
					if (pname.equals("word")) 
						hitStart = tokensList.size();
					tokensList.addAll( jsonUtils.getProperty(match, pname) );
					if (pname.equals("word")) 
						hitEnd = tokensList.size();
					
					// [3] -----------------------------
					
					// add RIGHT context					
					tokensList.addAll( jsonUtils.getProperty(rightContext, pname) );
					
					
					// add keyword in context (Kwic)
					kwic.addTokenPropertyName(pname);
					kwic.addTokenProperties(pname, tokensList);					
				}

				// store the start and end position of the matched token (=part [2] hereabove)
				// (= token that meets the query)
				kwic.setHitStart( hitStart );
				kwic.setHitEnd( hitEnd );
			 
			} catch (Exception e)
			{
				e.printStackTrace();
				throw e;
			}
		}
		
		return results;
	}
	
	
	
	/**
	 * Send an http request to Blacklab, and return the JSON output
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private JSONObject sendQuery(String url) throws Exception 
	{
		// start connection to url
		// and 
		// instantiate some convenient JSON methods
		
		JsonUtils jsonUtils = new JsonUtils();
		
		URLConnection connection =  new URL(url).openConnection();
		
		InputStream input = null;
		
		
		// get response
		
		try {
			input = connection.getInputStream();	
			return jsonUtils.getJsonFromStream(input);
		} 
		
		
		// but if something went wrong...
		
		catch (IOException e) {
			
			HttpURLConnection huc  = (HttpURLConnection) connection;
			input = huc.getErrorStream();
			
			try {
				JSONObject errorObject = jsonUtils.getJsonFromStream(input);
				System.err.println("Error: " + errorObject.toJSONString());
				throw new Exception(errorObject.toJSONString());
			} 
			catch (Exception e1) {
				throw e1;
			}
		}	
		finally { 
			if (input != null) input.close();
		}
		
	}
	
	

	

}