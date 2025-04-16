package org.ivdnt.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.ivdnt.fcs.endpoint.nederlab.NederlabQuery;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.Document;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.Token;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.TokenProperty;
import org.ivdnt.fcs.endpoint.nederlab.results.Hit;
import org.ivdnt.fcs.endpoint.nederlab.results.NederlabResultSet;
import org.ivdnt.fcs.mapping.ConversionEngine;
import org.ivdnt.fcs.results.Kwic;
import org.ivdnt.fcs.results.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCgn {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static ResultSet createResultSet(String tag, ServletContext servletContext) {
		ConcurrentHashMap<String, String> prefixMapping = new ConcurrentHashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2513250891269514148L;

			{
				put("t", "word");
			}
		};

		// Build NederlabResultSet with hits
		NederlabResultSet nederlabResultSet = new NederlabResultSet();
		List<TokenProperty> tokenProps = new ArrayList<>();

		Map<String, Object> posTokMap = new HashMap<String, Object>();
		posTokMap.put("mtasId", 144546);
		posTokMap.put("prefix", "pos");
		posTokMap.put("value", tag);
		posTokMap.put("positionStart", 21714);
		posTokMap.put("positionEnd", 21714);
		TokenProperty tPos = new TokenProperty(posTokMap);
		tokenProps.add(tPos);

		Map<String, Object> wordTokMap = new HashMap<String, Object>();
		wordTokMap.put("mtasId", 144547);
		wordTokMap.put("prefix", "t");
		wordTokMap.put("value", "testwoord");
		wordTokMap.put("positionStart", 21714);
		wordTokMap.put("positionEnd", 21714);
		TokenProperty tWord = new TokenProperty(wordTokMap);
		tokenProps.add(tWord);

		String documentKey = "testdoc";

		// build a Hit object
		// t.i.: a document with tokens (which form a context)
		// and some start/end position for the hit within that context

		Hit h = new Hit(tokenProps);
		Map<String, Object> dm = new HashMap<String, Object>();
		dm.put("NLCore_NLIdentification_nederlabID", "testid0000");
		dm.put("NLTitle_title", "testtitle");
		Document doc = new Document(dm);
		h.setDocumentKey(documentKey);
		h.setDocument(doc);
		h.setHitStart((Integer) wordTokMap.get("positionStart"));
		h.setHitEnd((Integer) wordTokMap.get("positionEnd"));
		int l = 0;

		// bleuh

		for (Token t1 : h.getTokens()) {
			if (t1.isContentToken() && t1.getStartPosition() == h.getHitStart()) {
				h.setHitEnd(l + (h.getHitEnd() - h.getHitStart()));
				h.setHitStart(l);
				break;
			}
			if (t1.isContentToken())
				l++;
		}

		nederlabResultSet.addResult(h);

		// ------
		Stream<org.ivdnt.fcs.results.Kwic> results = nederlabResultSet.getResults().stream()
				.map(z -> z.toKwic().translatePrefixes(prefixMapping)); // another ugly hack
		List<org.ivdnt.fcs.results.Kwic> hitsConverted = results.collect(Collectors.toList());
		// build FCS ResultSet

		ResultSet fcsResultSet = new ResultSet();
		fcsResultSet.setHits(hitsConverted);
		NederlabQuery q = new NederlabQuery(servletContext, "testserver", "testcorpus", "testquery", 0, 20, 0, null, null,
				"testenginenativeurltemplate", Collections.emptyList());
		fcsResultSet.setQuery(q);
		fcsResultSet.setTotalNumberOfResults(nederlabResultSet.getTotalNumberOfHits());
		return fcsResultSet;
	}

	private static String extractUniversalTag(ResultSet r) {
		List<Kwic> hits = r.getHits();
		Kwic hit = hits.get(0);
		List<String> ud = hit.getPropertyValues("universal_dependency");
		String udString = StringUtils.join(ud, ",");
		udString = "";
		List<String> propNames = hit.getTokenPropertyNames();
		for (String propName : propNames) {
			udString += propName + ":" + hit.getPropertyValues(propName);
		}
		return udString;

	}

	public static void testQueries(ServletContext servletContext, ConversionEngine convEng) {
		String tagsFileContents = new FileUtils(servletContext, "cgn.tagset").readToString();
		Scanner scanner = new Scanner(tagsFileContents);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("tags-converted.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			scanner.close();
			throw new RuntimeException("Unable to open file to write query test query results to. ", e);
		}
		while (scanner.hasNextLine()) {
			String tag = scanner.nextLine().trim();
			ResultSet r = createResultSet(tag, servletContext);
			convEng.translateIntoUniversalDependencies(r);

			String udTag = extractUniversalTag(r);
			writer.println(tag + "\t" + udTag);

		}
		scanner.close();
		writer.close();
	}

	/*
	 * private static ResultSet createResultSetBlackLab(String tag, ServletContext
	 * context) { ResultSet resultSet = new ResultSet(); List<Kwic> results = new
	 * ArrayList<Kwic>();
	 * 
	 * for (int i = 0; i < hits.size(); i++) { try { Kwic kwic = new Kwic();
	 * results.add(kwic);
	 * 
	 * JSONObject hit = (JSONObject) hits.get(i);
	 * 
	 * @SuppressWarnings("unchecked") HashMap<String,Object> doc =
	 * (HashMap<String,Object>) docs.get((String) hit.get("docPid"));
	 * 
	 * doc.forEach((k, v) -> kwic.getMetadata().put(k.toString(), v.toString())); //
	 * or put this in separate document info objects?
	 * 
	 * // the results consist of 3 distinct parts: // the match, its context to the
	 * left, and its context to the right
	 * 
	 * JSONObject leftContext = (JSONObject) hit.get("left"); JSONObject match =
	 * (JSONObject) hit.get("match"); JSONObject rightContext = (JSONObject)
	 * hit.get("right");
	 * 
	 * // initialize details about matched token
	 * 
	 * @SuppressWarnings("unchecked") Set<String> matchedTokenProperties =
	 * (Set<String>) match.keySet(); int hitStart = 0; int hitEnd = 0;
	 * 
	 * // now build tokens list // with [1] left context, [2] match, and [3] right
	 * context
	 * 
	 * for (String pname : matchedTokenProperties) { List<String> tokensList = new
	 * ArrayList<String>();
	 * 
	 * // [1] -----------------------------
	 * 
	 * // add LEFT context tokensList.addAll(JsonUtils.getProperty(leftContext,
	 * pname));
	 * 
	 * // [2] -----------------------------
	 * 
	 * // add MATCH // and note the start and end position of it if
	 * (pname.equals("word")) hitStart = tokensList.size();
	 * tokensList.addAll(JsonUtils.getProperty(match, pname)); if
	 * (pname.equals("word")) hitEnd = tokensList.size();
	 * 
	 * // [3] -----------------------------
	 * 
	 * // add RIGHT context tokensList.addAll(JsonUtils.getProperty(rightContext,
	 * pname));
	 * 
	 * // add keyword in context (Kwic) // NB: the tokensList is sorted, so each
	 * property has the same index // as the token it represents!
	 * 
	 * kwic.addTokenPropertyName(pname); kwic.setTokenProperties(pname, tokensList);
	 * }
	 * 
	 * // store the start and end position of the matched token (=part [2]
	 * hereabove) // (= token that meets the query) kwic.setHitStart(hitStart);
	 * kwic.setHitEnd(hitEnd);
	 * 
	 * } catch (Exception e) { throw new
	 * RuntimeException("Exception while parsing results.", e); } }
	 * 
	 * // build a resultSet
	 * 
	 * resultSet.setHits(results); //resultSet.setQuery(this);
	 * resultSet.setTotalNumberOfResults(100); return resultSet; }
	 */

}
