package org.ivdnt.fcs.endpoint.nederlab;

import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.nederlab.client.NederlabConstants;
import org.ivdnt.fcs.endpoint.nederlab.client.QueryTemplate;
import org.ivdnt.fcs.mapping.ConversionEngine;
import org.ivdnt.fcs.results.FcsSearchResultSet;
import org.ivdnt.fcs.results.ResultSet;

import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;


public class NederlabEndpointSearchEngine extends BasicEndpointSearchEngine
{
	
	String server = NederlabConstants.DEFAULT_SERVER;
	ConversionEngine conversion = null;
	QueryTemplate nederlabQueryTemplate;
		
	
	// ---------------------------------------------------------------------------------
	// constructors
	
	public NederlabEndpointSearchEngine(String server, ConversionEngine conversion, String nederlabQueryTemplate)
	{
		super();
		this.server = server;
		this.conversion = conversion;
		
		// instantiate a Nederlab query template (needed to post well formed query's to Nederlab)
		this.nederlabQueryTemplate = new QueryTemplate(nederlabQueryTemplate);
	}
	
	public NederlabEndpointSearchEngine() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	
	// ---------------------------------------------------------------------------------

	/**
	 * Prepare and start a Nederlab search:
	 * 
	 * 1. translate que FCS query into CQP
	 * 2. instantiate the Nederlab query 
	 * 3. send the query and build a FCS ResultSet  
	 */
	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException 
	{
		// translate FCS into CQP
		
		String cqpQuery = BasicEndpointSearchEngine.translateQuery(request, this.conversion);
		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request, "nederlab");
		
		
		// instantiate the Nederlab query 
		
		NederlabQuery nederlabQuery = 
				new NederlabQuery( 
						this.server, 
						fcsContextCorpus, 
						cqpQuery, 
						this.nederlabQueryTemplate);
		
		nederlabQuery.setStartPosition( request.getStartRecord()-1 ); // fcs begint bij 1 te tellen, nederlab bij 0 (?)
		nederlabQuery.setMaximumResults( request.getMaximumRecords() );

		
		// start the search and get the results
		
		try {
			ResultSet fcsResultSet = nederlabQuery.execute();

			return new FcsSearchResultSet(config, request, diagnostics, fcsResultSet);
		} catch (Exception e) {
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"The query execution failed by this CLARIN-FCS (nederlab) endpoint. " + nederlabQuery);
		}
	}
	
	// ---------------------------------------------------------------------------------
}
