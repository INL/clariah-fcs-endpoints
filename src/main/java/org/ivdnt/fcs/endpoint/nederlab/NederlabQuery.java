package org.ivdnt.fcs.endpoint.nederlab;

import org.ivdnt.fcs.endpoint.bls.BlacklabServerQuery;
import org.ivdnt.fcs.endpoint.bls.BlacklabServerResultSet;
import org.ivdnt.fcs.endpoint.nederlab.stuff.NederlabClient;
import java.util.*;
import java.util.stream.Collectors;

public class NederlabQuery extends clariah.fcs.Query
{
	Map<String,String> prefixMapping = new HashMap<String, String>() 
	{
		{
	    
	        put("t", "word");
	       
		}
	};
	
	public NederlabQuery(String server, String corpus, String cqp)
	{
		super(server, corpus, cqp);
		this.cqp = this.cqp.replaceAll("word *=", "t_lc="); // hm ugly hacks
		this.cqp = this.cqp.replaceAll("'", "\"");
		System.err.println("CQP to nederlab:" + this.cqp);
	}
	
	public clariah.fcs.ResultSet execute()
	{
		NederlabClient c = new NederlabClient();
		
	    List<clariah.fcs.Kwic> hits = 
	    		c.getResults(this.cqp, this.startPosition, this.maximumResults)
	    		.stream()
	    		.map(h -> h.toKwic().translatePrefixes(prefixMapping)) // another ugly hack
	    		.collect(Collectors.toList());
	    clariah.fcs.ResultSet bsrs = new clariah.fcs.ResultSet();
		bsrs.hits = hits;
		bsrs.query = this;
		System.err.println("Result set determined " + bsrs.toString());
		return bsrs;	
	}
}