package clariah.fcs.client;

import clariah.fcs.results.ResultSet;

/**
 * All engines will have a Query class that implements the Query abstract class
 * 
 * @author jesse
 *
 */
public abstract class Query 
{
	
	private String server = QueryConstants.defaultServer;	
	private String cqp;
	private String corpus;
	
	private int startPosition;
	private int maximumResults;
	private int totalNumberOfResults;
	
	
	// Abstract classes allow basic constructor and getters/setters implementation
	//
	// https://stackoverflow.com/questions/260666/can-an-abstract-class-have-a-constructor
	// https://stackoverflow.com/questions/4040921/can-we-put-getters-and-setters-in-abstract-classes
	
	// --------------------------------------------------------------------------------	

	/**
	 * Query object constructor
	 *  
	 * @param server, a server URL 
	 * 
	 * @param corpus, the name of a corpus to search (corpora names are declared
	 *                as Resource pid's in WEB-INF/endpoint-description.xml)
	 *                
	 * @param cqp, a CQL query string like [word="paard"]
	 * 
	 */
	public Query(String server, String corpus, String cqp)
	{
		this.server = server;
		this.corpus= corpus;
		this.cqp = cqp;
	}
	
	
	// --------------------------------------------------------------------------------
	// getters
	
	public String getServer() {
		return this.server;
	}
	
	public String getCorpus() {
		return this.corpus;
	}	
	
	public String getCqp() {
		return this.cqp;
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
	
	
	// --------------------------------------------------------------------------------	
	// setters
	
	public void setServer(String server) {
		this.server = server;
	}
	
	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	public void setCqp(String cqp) {
		this.cqp = cqp;
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
	
	public String toString() 
	{
		return String.format("Query(cqp=%s,server=%s,corpus=%s)",cqp,server,corpus);
	}

	
	// --------------------------------------------------------------------------------
	
	/**
	 * Start a search query (which as been prepared in the search() method of the chosen Engine)
	 * and return the results as a ResultSet
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract  ResultSet execute() throws Exception;
	
	
	// --------------------------------------------------------------------------------
}