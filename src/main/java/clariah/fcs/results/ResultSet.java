package clariah.fcs.results;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import clariah.fcs.client.Query;
import clariah.fcs.results.*;


/**
 * This class is used to store the search results of our different endpoints (Blacklab, Nederlab, ...)
 * as part of a FcsSearchResultSet (main result object) 
 * 
 * In the end, the ResultSet is packed into a SRUSearchResultSet,
 * which is the service output of every search
 *  
 * @author jesse
 *
 */
public class ResultSet 
{
	// A query object, which consists of:
	// - a server URL to send the query to
	// - the name of a corpus to search
	// - a CQP query
	private Query query;
	
	// The total number of results of a query
	private int totalNumberOfResults;
		
	// The text results, as a list of keywords in context (Kwic)	
	private List<Kwic> hits = new ArrayList<>();
	
	// The documents in which the hits were found
	private Map<String, Document> documents = new ConcurrentHashMap<>();
	
	// NOT IN USE
	//private int startPosition;
	//private int maximumRecords;
	
	
	// --------------------------------------------------------------------
	// getters
	
	public Query getQuery() {
		return query;
	}

	public int getTotalNumberOfResults() {
		return totalNumberOfResults;
	}
	
	public List<Kwic> getHits() {
		return hits;
	}
	
	public Map<String, Document> getDocuments() {
		return documents;
	}
	
	
	// --------------------------------------------------------------------
	// setters

	public void setTotalNumberOfResults(int totalNumberOfResults) {
		this.totalNumberOfResults = totalNumberOfResults;
	}
	
	public void setQuery(Query query) {
		this.query = query;
	}	

	public void setHits(List<Kwic> hits) {
		this.hits = hits;
	}	
	

	public void setDocuments(Map<String, Document> documents) {
		this.documents = documents;
	}
	
	public void addDocument(String id, Document document) {
		this.documents.put(id, document);
	}

	
	// --------------------------------------------------------------------
	
    public String toString() 
	{
		String s = String.format("Resultset(query=%s, size=%d)", query.toString(), hits.size());
		return s;	
	}
    
    
    // --------------------------------------------------------------------
}
