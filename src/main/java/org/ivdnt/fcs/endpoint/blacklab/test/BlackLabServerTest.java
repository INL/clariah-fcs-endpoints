package org.ivdnt.fcs.endpoint.blacklab.test;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import clariah.fcs.results.Kwic;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlackLabServerTest {
	// http://localhost:8080/blacklab-server-1.6.0/ezel/hits?patt=%22ezel%22
	/** The BlackLab Server url for searching "mycorpus" (not a real URL) */
	final static String BASE_URL = "http://localhost:8080/blacklab-server-1.6.0/ezel/";

	/** Fetch the specified URL and decode the returned JSON.
	 * @param url the url to fetch
	 * @return the page fetched
	 */
	

	/** Context of the hit is passed in arrays, per property
	 * (word/lemma/PoS). Right now we only want to display the 
	 * words. This is how we join the word array to a string.
	 * @param context context structure containing word, lemma, PoS.
	 * @return the words joined together with spaces.
	 */
	static String words(JSONObject context) {
		System.err.println("context key set:"  + context.keySet());
		JSONArray words = (JSONArray)context.get("word");
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < words.size(); i++) {
			if (b.length() > 0)
				b.append(" ");
			b.append((String)words.get(i));
		}
		return b.toString();
	}

	

	/** Show an array of hits in an HTML table.
	 * @param hits the hits structure from the JSON response
	 * @param docs the docInfos structure from the JSON response
	 */
	public static void showHits(JSONArray hits, JSONObject docs) {

		// Iterate over the hits.
		// We'll add elements to the html array and join it later to produce our
		// final HTML.
		StringBuilder html = new StringBuilder();
		html.append("<table><tr><th>Title</th><th>Keyword in context</th></tr>\n");
		for (int i = 0; i < hits.size(); i++) {
			JSONObject hit = (JSONObject)hits.get(i);

			// Add the document title and the hit information
			JSONObject doc = (JSONObject)docs.get((String)hit.get("docPid"));

			// Context of the hit is passed in arrays, per property
			// (word/lemma/PoS). Right now we only want to display the 
			// words. This is how we join the word array to a string.
			String left = words((JSONObject)hit.get("left"));
			String match = words((JSONObject)hit.get("match"));
			String right = words((JSONObject)hit.get("right"));

			html.append("<tr><td>" + (String)doc.get("title") + "</td><td>" + left +
					" <b>" + match + "</b> " + right + "</td></tr>\n");
		}
		html.append("</table>\n");
		System.out.println(html.toString()); // Join lines and output
	}

	
	/** Performs a search and shows the results.
	 * @param patt the pattern to search for
	 */
	

}