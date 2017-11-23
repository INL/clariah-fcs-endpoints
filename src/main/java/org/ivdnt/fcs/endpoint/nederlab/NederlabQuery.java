package org.ivdnt.fcs.endpoint.nederlab;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.ivdnt.fcs.endpoint.nederlab.client.NederlabClient;
import org.ivdnt.fcs.endpoint.nederlab.results.NederlabResultSet;

import clariah.fcs.results.ResultSet;

public class NederlabQuery extends clariah.fcs.client.Query
{
	ConcurrentHashMap<String,String> prefixMapping = new ConcurrentHashMap<String, String>() 
	{
		{
	        put("t", "word");
		}
	};
	
	// --------------------------------------------------------------------
	
	/**
	 * NederlabQuery constructor
	 * 
	 * @param server, a URL string
	 * 
	 * @param corpus, a corpus name, like 'opensonar' (those are declared
	 *                as Resource pid's in WEB-INF/endpoint-description.xml)
	 *                
	 * @param cqp, a query like [word='lopen']
	 * 
	 */
	public NederlabQuery(String server, String corpus, String cqp)
	{
		super(server, corpus, cqp);
		
		// make sure the CQL query
		// has the right quotes and parameter names
		
		String cqlQuery = this.getCqp();
		cqlQuery = cqlQuery.replaceAll("word *=", "t_lc="); // hm ugly hacks 
		cqlQuery = cqlQuery.replaceAll("'", "\"");
		this.setCqp( cqlQuery );
		
		System.err.println( "CQP to nederlab:" + this.getCqp() );
	}
	
	
	
	// --------------------------------------------------------------------
	
	/**
	 * Execute a prepared search (prepared in NederlabEndpointSearchEngine.search)
	 * and put the results into a FCS ResultSet
	 */
	public clariah.fcs.results.ResultSet execute()
	{
		
		// search
		
		NederlabClient nederlabClient = new NederlabClient();		
		NederlabResultSet nederlabResultSet = 
				nederlabClient.doSearch(this.getCqp(), this.getStartPosition(), this.getMaximumResults());
		
		
		// get results
		
	    List<clariah.fcs.results.Kwic> hits = 
	    		nederlabResultSet.getResults()
	    		.stream()
	    		.map(h -> h.toKwic().translatePrefixes(prefixMapping)) // another ugly hack
	    		.collect(Collectors.toList());
	    
	    
	    // build FCS ResultSet
	    
	    ResultSet fcsResultSet = new ResultSet();
		fcsResultSet.setHits ( hits );
		fcsResultSet.setQuery( this );
		fcsResultSet.setTotalNumberOfResults( nederlabResultSet.getTotalNumberOfHits() );
		
		System.err.println("Result set determined " + fcsResultSet.toString());
		return fcsResultSet;	
	}
}