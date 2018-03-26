package org.ivdnt.fcs.endpoint.blacklab;

import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;
import org.ivdnt.fcs.mapping.ConversionEngine;
import org.ivdnt.fcs.results.FcsSearchResultSet;
import org.ivdnt.fcs.results.ResultSet;

import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;


public class BlacklabServerEndpointSearchEngine  extends BasicEndpointSearchEngine
{

	
	// -----------------------------------------------------------------------
	// constructors
	
	
	public BlacklabServerEndpointSearchEngine(String server, ConversionEngine conversionEngine, String engineNativeUrlTemplate)
	{
		super(server,conversionEngine,engineNativeUrlTemplate);
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
		
		String query = BasicEndpointSearchEngine.translateQuery(request, this.getConversionEngine());
		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request, BlacklabConstants.DEFAULT_CORPUS);
		
		
		// instantiate the Blacklab query
		
		BlacklabServerQuery blacklabServerQuery = new BlacklabServerQuery(this.getServer(), fcsContextCorpus, query, this.getEngineNativeUrlTemplate());

		blacklabServerQuery.setStartPosition( request.getStartRecord()-1 ); // bij fcs beginnen ze bij 1 te tellen ?
		blacklabServerQuery.setMaximumResults( request.getMaximumRecords() );
		
		System.err.println("Query to blacklab server: " + blacklabServerQuery);
		
		
		// start the search and get the results
		
		try {
			ResultSet resultSet = blacklabServerQuery.execute();
			
			
			// translate the results POS back into universal dependencies
			
			this.getConversionEngine().translateIntoUniversalDependencies(resultSet);
			

			return new FcsSearchResultSet(config, request, diagnostics, resultSet);
			
		} catch (Exception e) {
			System.err.println("Rethrowing as SRU exception:" + e);
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"The query execution failed by this CLARIN-FCS (Blacklab Server) endpoint: " + e.getMessage() +  "; Query: " + blacklabServerQuery.toString());
		}
	}
}
