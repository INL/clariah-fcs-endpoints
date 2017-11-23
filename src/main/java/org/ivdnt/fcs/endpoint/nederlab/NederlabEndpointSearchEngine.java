package org.ivdnt.fcs.endpoint.nederlab;

import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.nederlab.client.NederlabConstants;

import clariah.fcs.mapping.ConversionEngine;
import clariah.fcs.results.FcsSearchResultSet;
import clariah.fcs.results.ResultSet;
import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;


public class NederlabEndpointSearchEngine  extends BasicEndpointSearchEngine
{
	
	String server = NederlabConstants.defaultServer;
	ConversionEngine conversion = null;
	
	
	// ---------------------------------------------------------------------------------
	// constructors
	
	public NederlabEndpointSearchEngine(String server, ConversionEngine conversion)
	{
		super();
		this.server = server;
		this.conversion = conversion;
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
		
		String query = BasicEndpointSearchEngine.translateQuery(request, conversion);
		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request, "nederlab");
		
		
		// instantiate the Nederlab query 
		
		NederlabQuery nederlabQuery = new NederlabQuery(server, fcsContextCorpus, query);

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
