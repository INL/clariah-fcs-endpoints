package org.ivdnt.fcs.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.ivdnt.fcs.results.ResultSet;

/**
 * All engines will have a Query class that implements the Query abstract class
 * 
 * @author jesse, peter
 *
 */
public abstract class Query {

	private String server = QueryConstants.DEFAULT_SERVER;
	private String cqpQuery;
	private String corpus;
	private String engineNativeUrl;

	private int startPosition;
	private int maximumResults;

	// Total number of results matching the query: this can be more than
	// number of results returned by one API call, which is thresholded by
	// maximumResults (maximumRecords argument in query)
	private int totalNumberOfResults;

	// Abstract classes allow basic constructor and getters/setters implementation
	//
	// https://stackoverflow.com/questions/260666/can-an-abstract-class-have-a-constructor
	// https://stackoverflow.com/questions/4040921/can-we-put-getters-and-setters-in-abstract-classes

	// --------------------------------------------------------------------------------

	/**
	 * Query object constructor
	 * 
	 * @param server,
	 *            a server URL
	 * 
	 * @param corpus,
	 *            the name of a corpus to search (corpora names are declared as
	 *            Resource pid's in WEB-INF/endpoint-description.xml)
	 * 
	 * @param cqp,
	 *            a CQL query string like [word="paard"]
	 * 
	 */
	public Query(String server, String corpus, String cqpQuery, String engineNativeUrlTemplate) {
		this.server = server;
		this.corpus = corpus;
		this.cqpQuery = cqpQuery;

		// From native URL based on template and URL-encoded query string
		try {
			this.engineNativeUrl = engineNativeUrlTemplate + URLEncoder.encode(this.cqpQuery, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Not able to encode query URL " + this.cqpQuery, e);
		}
	}

	// --------------------------------------------------------------------------------
	// getters

	public String getServer() {
		return this.server;
	}

	public String getCorpus() {
		return this.corpus;
	}

	public String getCqpQuery() {
		return this.cqpQuery;
	}

	public int getStartPosition() {
		return this.startPosition;
	}

	public int getMaximumResults() {
		return this.maximumResults;
	}

	public int getTotalNumberOfResults() {
		return this.totalNumberOfResults;
	}

	public String getEngineNativeUrl() {
		return engineNativeUrl;
	}

	// --------------------------------------------------------------------------------
	// setters

	public void setServer(String server) {
		this.server = server;
	}

	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	public void setCqpQuery(String cqpQuery) {
		this.cqpQuery = cqpQuery;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public void setMaximumResults(int maximumResults) {
		this.maximumResults = maximumResults;
	}

	public void setTotalNumberOfResults(int totalNumberOfResults) {
		this.totalNumberOfResults = totalNumberOfResults;
	}

	// --------------------------------------------------------------------------------

	public String toString() {
		return String.format("Query(cqp=%s,server=%s,corpus=%s)", cqpQuery, server, corpus);
	}

	// --------------------------------------------------------------------------------

	/**
	 * Start a search query (which as been prepared in the search() method of the
	 * chosen Engine) and return the results as a ResultSet
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract ResultSet execute() throws Exception;

	// --------------------------------------------------------------------------------
}