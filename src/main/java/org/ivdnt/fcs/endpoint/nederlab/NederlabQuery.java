package org.ivdnt.fcs.endpoint.nederlab;

import org.ivdnt.fcs.endpoint.bls.BlacklabServerQuery;
import org.ivdnt.fcs.endpoint.bls.BlacklabServerResultSet;
import org.ivdnt.fcs.endpoint.nederlab.stuff.NederlabClient;
import java.util.*;
import java.util.stream.Collectors;

public class NederlabQuery extends clariah.fcs.Query
{
	public NederlabQuery(String server, String corpus, String cqp)
	{
		super(server, corpus, cqp);
	}
	
	public clariah.fcs.ResultSet execute()
	{
		NederlabClient c = new NederlabClient();
		
	    List<clariah.fcs.Kwic> hits = 
	    		c.getResults(this.cqp, this.startPosition, this.maximumResults)
	    		.stream()
	    		.map(h -> h.toKwic())
	    		.collect(Collectors.toList());
	    clariah.fcs.ResultSet bsrs = new clariah.fcs.ResultSet();
		bsrs.hits = hits;
		bsrs.query = this;
		
		return bsrs;	
	}
}