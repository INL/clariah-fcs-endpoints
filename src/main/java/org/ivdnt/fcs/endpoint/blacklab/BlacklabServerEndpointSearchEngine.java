package org.ivdnt.fcs.endpoint.blacklab;

import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;
import org.ivdnt.fcs.mapping.ConversionEngine;
import org.ivdnt.fcs.results.FcsSearchResultSet;
import org.ivdnt.fcs.results.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;

public class BlacklabServerEndpointSearchEngine extends BasicEndpointSearchEngine {

	private final static Logger logger = LoggerFactory.getLogger(BlacklabServerEndpointSearchEngine.class);

	// -----------------------------------------------------------------------
	// constructors

	public BlacklabServerEndpointSearchEngine(String server, ConversionEngine conversionEngine,
			int restrictTotalNumberOfResults, String engineNativeUrlTemplate) {
		super(server, conversionEngine, restrictTotalNumberOfResults, engineNativeUrlTemplate);
	}

	// -----------------------------------------------------------------------

	/**
	 * Prepare and start a Blacklab search:
	 * 
	 * 1. translate the FCS query into CQP, 2. instantiate the BlackLab query 3.
	 * send the query and build a resultset
	 */
	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException {
		String query;
		// translate FCS into CQP
		try {
			query = BasicEndpointSearchEngine.translateQuery(request, this.getConversionEngine());
		} catch (Exception e) {
			logger.error("Rethrowing as SRU exception:" + e);
			throw new SRUException(SRUConstants.SRU_UNSUPPORTED_PARAMETER,
					"Error during translation of query from UD to corpus-specific tagset. The query execution failed by this CLARIN-FCS (Blacklab Server) endpoint: " + e.getMessage());
		}

		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request,
				"");

		// instantiate the Blacklab query
		// bij fcs beginnen ze bij 1 te tellen ?
		BlacklabServerQuery blacklabServerQuery = new BlacklabServerQuery(this.getServer(), fcsContextCorpus, query,
				request.getStartRecord() - 1, request.getMaximumRecords(), this.restrictTotalNumberOfResults, this.getEngineNativeUrlTemplate());

		logger.info("Query to blacklab server: " + blacklabServerQuery);

		// start the search and get the results
		ResultSet resultSet;
		try {
			resultSet = blacklabServerQuery.execute();
		}
		catch (Exception e) {
			logger.error("Rethrowing as SRU exception:" + e);
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"Error during execution of query. The query execution failed by this CLARIN-FCS (Blacklab Server) endpoint: " + e.getMessage()
							+ "; Query: " + blacklabServerQuery.toString());
		}

			// translate the results POS back into universal dependencies
		try {
			this.getConversionEngine().translateIntoUniversalDependencies(resultSet);
		} catch (Exception e) {
			logger.error("Rethrowing as SRU exception:" + e);
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"Error during back-translation to UD. The query execution failed by this CLARIN-FCS (Blacklab Server) endpoint: " + e.getMessage()
							+ "; Query: " + blacklabServerQuery.toString());
		}

			return new FcsSearchResultSet(config, request, diagnostics, resultSet);

	}
}
