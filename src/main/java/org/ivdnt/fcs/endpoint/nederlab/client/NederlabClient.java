package org.ivdnt.fcs.endpoint.nederlab.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.codehaus.plexus.util.CollectionUtils;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.Document;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.Token;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.TokenProperty;
import org.ivdnt.fcs.endpoint.nederlab.results.Hit;
import org.ivdnt.fcs.endpoint.nederlab.results.NederlabResultSet;
import org.ivdnt.util.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

/**
 * @author jesse, peter
 * 
 *         Zie ook
 *         https://github.com/meertensinstituut/mtas/blob/master/conf/solr/schemaOeaw.xml
 */

public class NederlabClient {


	// Variable to store server URL, initialized by constructor
	private String server;

	// When searching Nederlab for a word, we want to get results
	// in context, t.i. not only the word, but also a part of the text
	// in which it was found.
	// So we expect to find the word in the results surrounded
	// by a given number of words on its left side
	// and the same number of words on its right side.
	// This number is:

	private int contextSize = 8;

	// The Nederlab query template set for this client
	private QueryTemplate nederlabQueryTemplate;
	private QueryTemplate nederlabDocumentQueryTemplate;
	
	// Extra response fields, are send with query, to ask Nederlab to return these fields
	private List<String> nederlabExtraResponseFields;
	
	private ServletContext contextCache;
	
	private ObjectMapper mapper;
	
	// ------------------------------------------------------------------

	// Constructor

	public NederlabClient(ServletContext contextCache, QueryTemplate nederlabQueryTemplate, QueryTemplate nederlabDocumentQueryTemplate, String server, List<String> nederlabExtraResponseFields) {
		this.contextCache = contextCache;
		this.nederlabQueryTemplate = nederlabQueryTemplate;
		this.nederlabDocumentQueryTemplate = nederlabDocumentQueryTemplate;
		this.server = server;
		this.nederlabExtraResponseFields = nederlabExtraResponseFields;
		this.mapper = new ObjectMapper();
	}

	// ------------------------------------------------------------------

	/**
	 * perform a complete Nederlab search: build a query, send it, and parse the
	 * response
	 * 
	 * @param CQL
	 * @param start
	 * @param number
	 * @return
	 */
	public NederlabResultSet doSearch(String CQL, int start, int number) {
		String jsonHits = requestHits(CQL, start, number);
		// Parse the document keys from the response, and send new query, requesting document information
		Map<String,Document> docMap = parseAndRequestDocuments(jsonHits);
		
		// parse the response
		NederlabResultSet results = parseResults(jsonHits, docMap);
		return results;
	}

	private String requestHits(String CQL, int start, int number) {
		String cqlQuery = CQL.replaceAll("\"", "\\\\\\\\" + "\"");

		// fill in some values in the Query

		Map<String, String> queryTemplateValues = new ConcurrentHashMap<>();
		queryTemplateValues.put("_START_", new Integer(start).toString());
		queryTemplateValues.put("_NUMBER_", new Integer(number).toString());
		queryTemplateValues.put("_CONTEXT_", new Integer(this.contextSize).toString());
		queryTemplateValues.put("_QUERY_", cqlQuery);
		
		// Add extra response fields to query, so we ask Nederlab server to return these fields
		String jsonQuery = this.nederlabQueryTemplate.expandTemplate(queryTemplateValues);
		//String jsonQueryUpdated = addResponseFieldsToQuery(jsonQuery, nederlabExtraResponseFields);
		
		// send the query
		final long sendStartTime = System.currentTimeMillis();
		String jsonResults = sendQuery(jsonQuery);
		final long sendEndTime = System.currentTimeMillis();
		System.err.println("Request hits: " + (sendEndTime - sendStartTime) + " ms.");
		return jsonResults;
	}

	private String addResponseFieldsToQuery(String jsonQuery, List<String> extraResponseFields) {
		DocumentContext dc = JsonPath.parse(jsonQuery);
		for (String f : extraResponseFields) {
			dc = dc.add("$.response.documents.fields", f);
		}
		return dc.jsonString();
	}

	// ------------------------------------------------------------------

	/**
	 * post a query string to Nederlab
	 * 
	 * @param query
	 * @return
	 */
	public String sendQuery(String query) {
		StringBuilder response = new StringBuilder();
		try {
			System.err.println("Now connecting to server to send POST request: " + this.server);
			URL obj = new URL(this.server);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add request header

			con.setRequestMethod("POST");
			// con.setRequestProperty("User-Agent", USER_AGENT);
			String brokerKey = new FileUtils(contextCache, "key.txt").readConfigFileAsString().trim();
			con.setRequestProperty("X-Broker-key", brokerKey);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			// Send post request

			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

			osw.write(query);
			osw.flush();
			osw.close();

			int responseCode = con.getResponseCode();

			System.err.println("Query : " + query);
			System.err.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while sending query " + query, e);
		}
		return response.toString();
	}

	/**
	 * parse the results of a Nederlab query and return those in a NederlabResultSet
	 * object
	 * 
	 * @param jsonResult,
	 *            as a String
	 * @return a NederlabResultSet object
	 */
	public NederlabResultSet parseResults(String jsonResult, Map<String, Document> docMap) {
		// The result string from Nederlab can be QUITE BIG,
		// which is because it's formated in highly hierarchical Json,
		// in which each text result is split up into its separate tokens,
		// with pos-tag details, positionStart, positionEnd, etc. etc...

		System.err.println(">>> result string length = " + jsonResult.length());

		// the response looks like this:
		// TODO 

		// we will store the response in a NederlabResultSet object

		NederlabResultSet nederlabResultSet = new NederlabResultSet();

		// useful reference about JSON parsing:
		// http://www.baeldung.com/guide-to-jayway-jsonpath

		DocumentContext context = JsonPath.parse(jsonResult);

		try {
			// get the stats out of the JSON response

			String totalNrOfHits = (context.read("$['mtas']['list'][0]['total']")).toString();
			nederlabResultSet.setTotalNumberOfHits(Integer.parseInt(totalNrOfHits));

			System.err.println(">>> total number of hits = " + nederlabResultSet.getTotalNumberOfHits());

			// process the 'documents' part of the JSON response

			/*JSONArray documents = (JSONArray) context.read("$['documents']");

			Map<String, Document> docMap = new ConcurrentHashMap<>();

			for (Object d : documents) {
				String docJ = this.mapper.writeValueAsString(d); // ugly reserialization!!!
				Document doc = this.mapper.readValue(docJ, Document.class);
				@SuppressWarnings("unchecked")
				Map<String,Object> d_map = (Map<String,Object>) d;
				Document doc = new Document(d_map);
				docMap.put(doc.getField("NLCore_NLIdentification_nederlabID"), doc);
			}*/



			// Parse the list of hits

			Object kwicList = context.read("$['mtas']['list'][0]['list'][*]");
			
			// First, go through list of hits to get all document keys
			// TODO
			
			// Make new request to server: get document keys


			JSONArray hits = (JSONArray) kwicList;
			System.err.println(
					">>> number of hits in current resultset = " + hits.size());
			for (int k = 0; k < hits.size(); k++) {
				
				Object hit = hits.get(k);
				List<TokenProperty> tokenProps = new ArrayList<>();
				@SuppressWarnings("unchecked")
				Map<String,Object> hitMap = (Map<String,Object>) hit;
				String documentKey = (String) hitMap.get("documentKey");
				JSONArray tokens = (JSONArray) hitMap.get("tokens");
				// a hit contains a list of tokens:
				//
				// the size of the list is:
				// X tokens on the left
				// + 1 token in the middle (the hit)
				// + X tokens on the right,
				// with X being the value of NederlabClient.contextSize
				for (int l = 0; l < tokens.size(); l++) {
					@SuppressWarnings("unchecked")
					Map<String,Object> tok = (Map<String,Object>) (tokens.get(l));
					//String tokJ = this.mapper.writeValueAsString(tok); // ugly reserialization!!!
					//TokenProperty t = this.mapper.readValue(tokJ, TokenProperty.class);
					TokenProperty t = new TokenProperty(tok);
					tokenProps.add(t);
				}

				// build a Hit object
				// t.i.: a document with tokens (which form a context)
				// and some start/end position for the hit within that context

				Hit h = new Hit(tokenProps);
				h.setDocumentKey(documentKey);
				h.setDocument(docMap.get(documentKey));
				h.setHitStart((Integer) hitMap.get("startPosition"));
				h.setHitEnd((Integer) hitMap.get("endPosition"));
				int l = 0;

				// bleuh

				for (Token t : h.getTokens()) {
					if (t.isContentToken() && t.getStartPosition() == h.getHitStart()) {
						h.setHitEnd(l + (h.getHitEnd() - h.getHitStart()));
						h.setHitStart(l);
						break;
					}
					if (t.isContentToken())
						l++;
				}

				nederlabResultSet.addResult(h);
			}

		} catch (Exception e) {
			throw new RuntimeException("Exception while parsing json: " + context, e);
		}
		return nederlabResultSet;
	}
	
	/**
	 * Parse the document keys from the response, and send new query, requesting document information
	 * 
	 * @param jsonResult,
	 *            as a String
	 * @param documentQueryTemplate, String
	 * @return a NederlabResultSet object
	 * @throws IOException 
	 */
	public Map<String,Document> parseAndRequestDocuments(String jsonHits) {

		DocumentContext hitsContext = JsonPath.parse(jsonHits);
		HashSet<String> documentKeysSet = new HashSet<String>();
		String documentKeysString = "";

		try {
			// Parse the list of hits
			List<String> documentKeys = hitsContext.read("$['mtas']['list'][0]['list'][*]['documentKey']");
			documentKeysSet = new HashSet<>(documentKeys);
			documentKeysString = mapper.writeValueAsString(documentKeysSet);
				
		}
		catch (Exception e) {
			throw new RuntimeException("Exception while parsing json: " + hitsContext, e);
		}
		
		// Create new request and send
		// fill in some values in the Query
		Map<String, String> queryTemplateValues = new ConcurrentHashMap<>();
		queryTemplateValues.put("_DOCKEY_", documentKeysString);
		// Set number of doc keys, so we get back all documents in one response
		queryTemplateValues.put("_NUMBERDOC_", new Integer(documentKeysSet.size()).toString());
		
		// Add extra response fields to query, so we ask Nederlab server to return these fields
		String jsonQuery = this.nederlabDocumentQueryTemplate.expandTemplate(queryTemplateValues);
		
		// send the query
		final long sendStartTime = System.currentTimeMillis();
		String jsonDocs= sendQuery(jsonQuery);
		final long sendEndTime = System.currentTimeMillis();
		System.err.println("Request documents: " + (sendEndTime - sendStartTime) + " ms.");
		
		// Process the 'docs' part of the JSON response
		DocumentContext docsContext = JsonPath.parse(jsonDocs);
		// First, check if documents for all keys have been returned
		List<String> documentKeysRetrieved = docsContext.read("$['response']['docs'][*]['NLCore_NLIdentification_nederlabID']");
		if(!equalsSet(documentKeysSet,documentKeysRetrieved)) {
			throw new RuntimeException("Documents returned by server do not match document keys from hits query!");
		}
		// Now, really retrieve documents
		JSONArray documents = docsContext.read("$['response']['docs'][*]");
		Map<String, Document> docMap = new ConcurrentHashMap<>();

		for (Object d: documents) {
			@SuppressWarnings("unchecked")
			Map<String,Object> dm = (Map<String,Object>) d;
			Document doc = new Document(dm);
			docMap.put(doc.getField("NLCore_NLIdentification_nederlabID"), doc);
		}
		return docMap;
	}
	
	public static <T> boolean equalsSet(HashSet<T> set1, List<T> list2) {
	    return set1.equals(new HashSet<>(list2));
	}

}