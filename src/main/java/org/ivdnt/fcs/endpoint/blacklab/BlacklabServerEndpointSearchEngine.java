package org.ivdnt.fcs.endpoint.blacklab;

import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;

import clariah.fcs.mapping.ConversionEngine;
import clariah.fcs.results.FcsSearchResultSet;
import clariah.fcs.results.ResultSet;
import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;


public class BlacklabServerEndpointSearchEngine  extends BasicEndpointSearchEngine
{
	String server = BlacklabConstants.defaultServer;
	ConversionEngine conversion = null;
	
	
	// -----------------------------------------------------------------------
	// constructors
	
	public BlacklabServerEndpointSearchEngine()
	{
		super();
	}
	
	public BlacklabServerEndpointSearchEngine(String server)
	{
		super();
		this.server = server;
	}
	
	public BlacklabServerEndpointSearchEngine(String server, ConversionEngine conversion)
	{
		super();
		this.server = server;
		this.conversion = conversion;
	}
	
	
	// -----------------------------------------------------------------------
	
	/**
	 * Prepare and start a Blacklab search:
	 * 
	 * 1. translate the FCS query into CQP,
	 * 2. instantiate the BlackLab query
	 * 3. send the query and build a resultset
	 */
	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException 
	{
		// translate FCS into CQP
		
		String query = BasicEndpointSearchEngine.translateQuery(request, conversion);
		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request, BlacklabConstants.defaultCorpus);
		
		
		// instantiate the Blacklab query
		
		BlacklabServerQuery blacklabServerQuery = new BlacklabServerQuery(this.server, fcsContextCorpus, query);

		blacklabServerQuery.setStartPosition( request.getStartRecord()-1 ); // bij fcs beginnen ze bij 1 te tellen ?
		blacklabServerQuery.setMaximumResults( request.getMaximumRecords() );
		
		System.err.println("Query to blacklab server: " + blacklabServerQuery);
		
		
		// start the search and get the results
		
		try {
			ResultSet resultSet = blacklabServerQuery.execute();

			return new FcsSearchResultSet(config, request, diagnostics, resultSet);
			
		} catch (Exception e) {
			System.err.println("Rethrowing as SRU exception:" + e);
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"The query execution failed by this CLARIN-FCS (Blacklab Server) endpoint: " + e.getMessage() +  "; Query: " + blacklabServerQuery.toString());
		}
	}
}
