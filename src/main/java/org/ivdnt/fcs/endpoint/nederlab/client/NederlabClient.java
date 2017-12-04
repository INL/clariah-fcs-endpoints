package org.ivdnt.fcs.endpoint.nederlab.client;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.ivdnt.fcs.endpoint.nederlab.objectmapper.Document;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.HitIterator;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.Token;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.TokenProperty;
import org.ivdnt.fcs.endpoint.nederlab.results.Hit;
import org.ivdnt.fcs.endpoint.nederlab.results.NederlabResultSet;
import org.ivdnt.util.FileUtils;
import org.ivdnt.util.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

/**
 * @author jesse
 * 
 * Zie ook https://github.com/meertensinstituut/mtas/blob/master/conf/solr/schemaOeaw.xml 
 */

public class NederlabClient 
{
	
	// Instantiate a mapper to convert a JSON response object into a JAVA object
	
	private ObjectMapper mapper = new ObjectMapper();
	
	
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


	// ------------------------------------------------------------------
	
	// Constructor
	
	public NederlabClient(QueryTemplate nederlabQueryTemplate) {
		
		this.nederlabQueryTemplate = nederlabQueryTemplate;
	}
	
	
	
	// ------------------------------------------------------------------
		
	/**
	 * perform a complete Nederlab search: 
	 * build a query, send it, and parse the response
	 * 
	 * @param CQL
	 * @param start
	 * @param number
	 * @return
	 */
	public NederlabResultSet doSearch(
			String CQL,  
			int start, int number)
	{
		String cqlQuery = CQL.replaceAll("\"", "\\\\\\\\" + "\"");		
		
		// fill in some values in the Query
		
		Map<String, String> queryTemplateValues = new ConcurrentHashMap<>();
		queryTemplateValues.put("_START_", new Integer(start).toString());
		queryTemplateValues.put("_NUMBER_", new Integer(number).toString());
		queryTemplateValues.put("_CONTEXT_", new Integer(this.contextSize).toString());
		queryTemplateValues.put("_QUERY_", cqlQuery);
		
		String jsonQuery = this.nederlabQueryTemplate.expandTemplate(queryTemplateValues);
		
		// send the query
		
		String jsonResults = sendQuery(jsonQuery);	
		
		// parse the response
		
		return parseResults(jsonResults);
	}
	
	
	// ------------------------------------------------------------------
	
	
	
	/**
	 * post a query string to Nederlab
	 * 
	 * @param query
	 * @return
	 */
	public String sendQuery(String query)
	{
		try
		{
			URL obj = new URL(NederlabConstants.NEDERLAB_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			//add request header
			
			con.setRequestMethod("POST");
			//con.setRequestProperty("User-Agent", USER_AGENT);
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
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		} 
		catch (Exception e)	{

			Utils.printStackTrace(e);
			return null;
		}	
	}
	
		
	/**
	 * parse the results of a Nederlab query
	 * and return those in a NederlabResultSet object
	 * 
	 * @param jsonResult, as a String
	 * @return a NederlabResultSet object
	 */
	public NederlabResultSet parseResults(String jsonResult)
	{
		// The result string from Nederlab can be QUITE BIG,
		// which is because it's formated in highly hierarchical Json, 
		// in which each text result is split up into its separate tokens, 
		// with pos-tag details, positionStart, positionEnd, etc. etc...
		
		System.err.println(">>> result string length = " + jsonResult.length());		
		
		// the response looks like this:
		//
		// {
		//     "status": "ok",
		//	   "documents": [
		//		   {
		//		   "NLProfile_name": "nederlabTitleProfile",
		//		   "NLCore_NLIdentification_nederlabID": "c242846c-7df1-47e0-8a8e-b9e37108e656",
		//		   "NLCore_NLAdministrative_sourceCollection": "EDBO",
		//		   "NLTitle_title": "Algemeen magazyn van wetenschap, konst en smaak [18] [18]",
		//		   "NLTitle_yearOfPublicationMin": 1785,
		//		   "NLTitle_yearOfPublicationMax": 1785
		//		   },
		//
		//     ...
		//        
		//	   ],
		//
		//	   "stats": {
		//     "total": 10329,					// this is the total number of hits
		//	   "start": 0
		//     },
		//
		//	   "mtas": {"kwic": [{
		//     "key": "tekst",
		//     "list": [						// list of documents
		//         {							// one document
		
		//         "documentKey": "c242846c-7df1-47e0-8a8e-b9e37108e656",
		//         "documentTotal": 14490,
		//         "documentMinPosition": 0,
		//         "documentMaxPosition": 116915,
		//         "list": [					// list of sentences of one document
		//             {						// one sentence
		//             "startPosition": 17,
		//             "endPosition": 17,
		//             "tokens": [
		//                 {					// one token!
		//                 "mtasId": 110,
		//                 "prefix": "t",		//   this can have value 't'=token, or 'pos', or 'lemma'
		//                 "value": "en",		//   (each token is represented by those 3 parts
		//                 "positionStart": 14,	//	  like we can see it here)
		//                 "positionEnd": 14,
		//                 "parentMtasId": 128
		//                 },
		//                 {
		//                 "mtasId": 113,
		//                 "prefix": "pos",
		//                 "value": "VG",
		//                 "positionStart": 14,
		//                 "positionEnd": 14
		//                 },
		//                 {
		//                 "mtasId": 114,
		//                 "prefix": "lemma",
		//                 "value": "en",
		//                 "positionStart": 14,
		//                 "positionEnd": 14
		//                 },
		//
		//				   ...
		//
		//                 {
		//                 "mtasId": 82,
		//                 "prefix": "lemma",
		//                 "value": ",",
		//                 "positionStart": 9,
		//                 "positionEnd": 9
		//                 }
		//              ]					// end of list of tokens of one sentence
		//             }					// end of one sentence
		//        ]							// end of list of sentences of one document
		//    }								// end of one document
		//  ]								// end of list of documents
		// }]}      						// end of mtas
		//}
		
		
		// we will store the response in a NederlabResultSet object
		
		NederlabResultSet nederlabResultSet = new NederlabResultSet();		
				
		
		// useful reference about JSON parsing:
		// http://www.baeldung.com/guide-to-jayway-jsonpath
		
		DocumentContext context = JsonPath.parse(jsonResult);
		
		
		try
		{			
			// get the stats out of the JSON response
			
			String totalNrOfHits = ( context.read("$['stats']['total']") ).toString();			
			nederlabResultSet.setTotalNumberOfHits( Integer.parseInt(totalNrOfHits) );
			
			System.err.println(">>> total number of hits = "+nederlabResultSet.getTotalNumberOfHits());
						
						
			// process the 'documents' part of the JSON response
			
			JSONArray documents = (JSONArray) context.read("$['documents']");

			List<Document> docs = new ArrayList<Document>();
			Map<String,Document> docMap = new ConcurrentHashMap<>();
			
			for (Object d: documents)
			{
				String docJ = this.mapper.writeValueAsString(d); // ugly reserialization!!!
				Document doc = this.mapper.readValue(docJ, Document.class);
				docs.add(doc);
			}

			for (Document d: docs)
			{
				docMap.put(d.NLCore_NLIdentification_nederlabID, d);
			}

			
			// ---- for debugging --------------------------			
			if (false) 
				return nederlabResultSet;			
			// ---------------------------------------------
			
			
			// now parse the 'keywords in context' part of the JSON response
			
			
			
			// we should get only one list of documents, consisting of 10 documents
			
			Object kwicList = context.read("$[*]['kwic'][*]['list']");

			if (kwicList instanceof net.minidev.json.JSONArray)
			{
				JSONArray kwicListArr = (JSONArray) kwicList;

				for (int i=0; i < kwicListArr.size(); i++)
				{
					JSONArray listOfDocumentsWithHits = (JSONArray) kwicListArr.get(i);
					
					System.err.println(">>> number of documents in current resultset = "+listOfDocumentsWithHits.size());

					for (int j=0; j < listOfDocumentsWithHits.size(); j++)
					{
						
						// each document has its documentKey (= nederlabID)
						
						Map documentWithListOfHits = (Map) listOfDocumentsWithHits.get(j);
						String documentKey = (String) documentWithListOfHits.get("documentKey");
						
						
						
						// a document contains a list of tokens:
						// 
						// the size of the list is:
						//     X tokens on the left
						//   + 1 token in the middle (the hit)
						//   + X tokens on the right,
						// with X being the value of NederlabClient.contextSize

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
								String tokJ = this.mapper.writeValueAsString(tok); // ugly reserialization!!!
								TokenProperty t = this.mapper.readValue(tokJ, TokenProperty.class);
								tokenProps.add(t);
							}
							
							
							// build a Hit object 
							// t.i.: a document with tokens (which form a context)
							// and some start/end position for the hit within that context
							
							Hit h = new Hit(tokenProps);
							h.setDocumentKey( documentKey );
							h.setDocument( docMap.get(documentKey) );
							h.setHitStart( (Integer) hitMap.get("startPosition") );
							h.setHitEnd( (Integer) hitMap.get("endPosition") );
							int l=0;

							// bleuh

							for ( Token t: h.getTokens() )
							{
								if (t.isContentToken() && t.getStartPosition() == h.getHitStart())
								{
									h.setHitEnd( l + (h.getHitEnd() - h.getHitStart()) );
									h.setHitStart( l );
									break;
								}
								if (t.isContentToken() )
									l++;
							}
							
							nederlabResultSet.addResult(h);
						}
					}
				}
				
				
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			System.err.println(context);
		}
		return nederlabResultSet;
	}

	
	
	// ------------------------------------------------------------------
	// NOT IN USE
	
	public String queryNederlab(Object o)
	{
		try {
			String s = this.mapper.writeValueAsString(o);
			return sendQuery(s);
		} catch (JsonProcessingException e) 
		{
			Utils.printStackTrace(e);
		}
		return null;
	}
	
	public Stream<Hit> getHits(String CQL)
	{
		Iterator<Hit> hits = new HitIterator(this, CQL);
		Iterable<Hit> iterable = () -> hits;
		Stream<Hit> targetStream = StreamSupport.stream(iterable.spliterator(), false);
		return targetStream;
	}
	

	Map<String,Object> readQueryAsObject(String fileName)
	{
		try {
			Map<String,Object> userData =  (Map<String,Object>) this.mapper.readValue(new File(fileName), Map.class);
			String x = this.mapper.writeValueAsString(userData);
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
			Map<String,Object> userData =  (Map<String,Object>) this.mapper.readValue(new File(fileName), Map.class);
			String x = this.mapper.writeValueAsString(userData);
			System.err.println(x);
			return this.mapper.writeValueAsString(userData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	// ------------------------------------------------------------------
	// test only
	
	public void testJSONQuery(String query)
	{
		String jsonResult = sendQuery(query);
		parseResults(jsonResult);
	}

	public void testCQLQuery(String CQL)
	{
		HitIterator hi = new HitIterator(this, CQL);
		while (hi.hasNext())
		{
			System.out.println(hi.next());
		}
	}
	
	public void queryCQLFromFile(String fileName)
	{
		String cql = new FileUtils(fileName).readStringFromFile().trim();
		testCQLQuery(cql);
	}
	
}