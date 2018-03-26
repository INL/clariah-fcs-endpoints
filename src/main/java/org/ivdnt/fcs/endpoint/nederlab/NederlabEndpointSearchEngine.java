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
	
	QueryTemplate nederlabQueryTemplate;
		
	
	// ---------------------------------------------------------------------------------
	// constructors
	
	
	public NederlabEndpointSearchEngine(String server, ConversionEngine conversionEngine, String nederlabQueryTemplate, String engineNativeUrlTemplate)
	{
		super(server, conversionEngine, engineNativeUrlTemplate);
		
		// instantiate a Nederlab query template (needed to post well formed query's to Nederlab)
		this.nederlabQueryTemplate = new QueryTemplate(nederlabQueryTemplate);

	}
	
	
	
	// ---------------------------------------------------------------------------------

	/**
	 * Prepare and start a Nederlab search:
	 * 
	 * 1. translate the FCS query into CQP
	 * 2. instantiate the Nederlab query 
	 * 3. send the query and build a FCS ResultSet  
	 */
	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException 
	{
		// translate FCS into CQP
		
		String cqpQuery = BasicEndpointSearchEngine.translateQuery(request, this.getConversionEngine());
		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request, "nederlab");
		
		
		// instantiate the Nederlab query 
		
		NederlabQuery nederlabQuery = 
				new NederlabQuery( 
						this.getServer(), 
						fcsContextCorpus, 
						cqpQuery, 
						this.nederlabQueryTemplate,
						this.getEngineNativeUrlTemplate());
		
		nederlabQuery.setStartPosition( request.getStartRecord()-1 ); // fcs begint bij 1 te tellen, nederlab bij 0 (?)
		nederlabQuery.setMaximumResults( request.getMaximumRecords() );

		
		// start the search and get the results
		
		try {
			ResultSet fcsResultSet = nederlabQuery.execute();
			
			
			// translate the results POS back into universal dependencies
			
			this.getConversionEngine().translateIntoUniversalDependencies(fcsResultSet);
			

			return new FcsSearchResultSet(config, request, diagnostics, fcsResultSet);
			
		} catch (Exception e) {
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"The query execution failed by this CLARIN-FCS (nederlab) endpoint. " + nederlabQuery);
		}
	}
	
	// ---------------------------------------------------------------------------------
}
