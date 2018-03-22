package org.ivdnt.fcs.endpoint.nederlab;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.ivdnt.fcs.endpoint.nederlab.client.NederlabClient;
import org.ivdnt.fcs.endpoint.nederlab.client.QueryTemplate;
import org.ivdnt.fcs.endpoint.nederlab.results.NederlabResultSet;
import org.ivdnt.fcs.results.ResultSet;

public class NederlabQuery extends org.ivdnt.fcs.client.Query
{
	
	// template needed to build a well formed Nederlab query
	
	private QueryTemplate nederlabQueryTemplate;
	
	
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
	public NederlabQuery(String server, String corpus, 
			String cqpQuery, QueryTemplate nederlabQueryTemplate)
	{
		super(server, corpus, cqpQuery);
		
		
		// template to build Nederlab query's
		
		this.nederlabQueryTemplate = nederlabQueryTemplate;
		
		
		// make sure the CQL query
		// has the right quotes and parameter names
		
		String cqlQuery = this.getCqpQuery();
		cqlQuery = cqlQuery.replaceAll("word *=", "t_lc="); // hm ugly hacks 
		cqlQuery = cqlQuery.replaceAll("'", "\"");
		this.setCqpQuery( cqlQuery );
		
		System.err.println( "CQP to nederlab:" + this.getCqpQuery() );
	}
	
	
	// --------------------------------------------------------------------
		
	/**
	 * Execute a prepared search (prepared in NederlabEndpointSearchEngine.search)
	 * and put the results into a FCS ResultSet
	 */
	public org.ivdnt.fcs.results.ResultSet execute()
	{
		
		// search
		
		NederlabClient nederlabClient = new NederlabClient( this.nederlabQueryTemplate, this.getServer());	
		
		NederlabResultSet nederlabResultSet = 
				nederlabClient.doSearch(
						this.getCqpQuery(),
						this.getStartPosition(), 
						this.getMaximumResults());
		
		
		// get results
		
	    List<org.ivdnt.fcs.results.Kwic> hits = 
	    		nederlabResultSet.getResults()
	    		.stream()
	    		.map(h -> h.toKwic().translatePrefixes( this.prefixMapping )) // another ugly hack
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