package org.ivdnt.fcs.client;

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
	private String engineNativeUrlTemplate;
	private String engineNativeUrl;

	private int startPosition;
	private int maximumResults;

	// Total number of results matching the query: this can be more than
	// number of results returned by one API call, which is thresholded by
	// maximumResults (maximumRecords argument in query)
	private int totalNumberOfResults;
	
	
	// This variable restricts the total number of results that can ever be retrieved
	// for a query, regardless of which page you are on.
	// Restrictions are applied on the get functions of start position, max results and total results,
	// so query to server is changed accordingly.
	private boolean restrict;
	private int restrictTotalNumberOfResults;

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
	public Query(String server, String corpus, String cqpQuery, int startPosition, int maximumResults, int restrictTotalNumberOfResults, 
			String engineNativeUrlTemplate) {
		this.server = server;
		this.corpus = corpus;
		this.cqpQuery = cqpQuery;
		this.startPosition = startPosition;
		this.maximumResults = maximumResults;
		this.setEngineNativeUrlTemplate(engineNativeUrlTemplate);
		
		this.restrictTotalNumberOfResults = restrictTotalNumberOfResults;
		this.restrict = this.restrictTotalNumberOfResults != 0 ? true : false;

	}

	// --------------------------------------------------------------------------------
	// getters

	/**
	 * Start a search query (which as been prepared in the search() method of the
	 * chosen Engine) and return the results as a ResultSet
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract ResultSet execute() throws Exception;

	public String getCorpus() {
		return this.corpus;
	}

	public String getCqpQuery() {
		return this.cqpQuery;
	}

	public String getEngineNativeUrl() {
		return engineNativeUrl;
	}

	public String getEngineNativeUrlTemplate() {
		return engineNativeUrlTemplate;
	}

	public int getMaximumResults() {
		if (this.restrict && (this.startPosition + this.maximumResults > this.restrictTotalNumberOfResults)) {
			return restrictTotalNumberOfResults - this.startPosition;
		}
		else {
			return this.maximumResults;
		}
	}

	public String getServer() {
		return this.server;
	}

	// --------------------------------------------------------------------------------
	// setters

	public int getStartPosition() {
		if (this.restrict && (this.startPosition > this.restrictTotalNumberOfResults)) {
			return restrictTotalNumberOfResults;
		}
		else {
			return this.startPosition;
		}
	}

	public int getTotalNumberOfResults() {
		if (this.restrict && (this.totalNumberOfResults > this.restrictTotalNumberOfResults)) {
			return restrictTotalNumberOfResults;
		}
		else {
			return this.totalNumberOfResults;
		}
	}

	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	public void setCqpQuery(String cqpQuery) {
		this.cqpQuery = cqpQuery;
	}

	public void setEngineNativeUrl(String engineNativeUrl) {
		this.engineNativeUrl = engineNativeUrl;
	}

	public void setEngineNativeUrlTemplate(String engineNativeUrlTemplate) {
		this.engineNativeUrlTemplate = engineNativeUrlTemplate;
	}

	public void setMaximumResults(int maximumResults) {
		this.maximumResults = maximumResults;
	}

	// --------------------------------------------------------------------------------

	public void setServer(String server) {
		this.server = server;
	}

	// --------------------------------------------------------------------------------

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public void setTotalNumberOfResults(int totalNumberOfResults) {
		this.totalNumberOfResults = totalNumberOfResults;
	}

	public String toString() {
		return String.format("Query(cqp=%s,server=%s,corpus=%s)", cqpQuery, server, corpus);
	}

	// --------------------------------------------------------------------------------
}