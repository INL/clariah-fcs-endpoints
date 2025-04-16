package org.ivdnt.fcs.endpoint.nederlab;

import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.nederlab.client.QueryTemplate;
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

public class NederlabEndpointSearchEngine extends BasicEndpointSearchEngine {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private QueryTemplate nederlabQueryTemplate;
	private QueryTemplate nederlabDocumentQueryTemplate;
	private List<String> nederlabExtraResponseFields;
	private ServletContext servletContext;

	// ---------------------------------------------------------------------------------
	// constructors

	public NederlabEndpointSearchEngine(ServletContext servletContext, String server, ConversionEngine conversionEngine,
			int restrictTotalNumberOfResults, String nederlabQueryTemplate, String nederlabDocumentQueryTemplate,
			String engineNativeUrlTemplate,	List<String> nederlabExtraResponseFields) {
		super(server, conversionEngine, restrictTotalNumberOfResults, engineNativeUrlTemplate);

		// instantiate a Nederlab query template (needed to post well formed query's to
		// Nederlab)
		this.servletContext = servletContext;
		this.nederlabQueryTemplate = new QueryTemplate(nederlabQueryTemplate);
		this.nederlabDocumentQueryTemplate = new QueryTemplate(nederlabDocumentQueryTemplate);
		this.nederlabExtraResponseFields = nederlabExtraResponseFields;

	}

	// ---------------------------------------------------------------------------------

	/**
	 * Prepare and start a Nederlab search:
	 * 
	 * 1. translate the FCS query into CQP 2. instantiate the Nederlab query 3. send
	 * the query and build a FCS ResultSet
	 */
	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException {

		/*
		 * TestCgn.testQueries(servletContext, this.getConversionEngine()); if (true) {
		 * return null; }
		 */

		String cqpQuery = "";
		// translate FCS into CQP
		try {
			cqpQuery = BasicEndpointSearchEngine.translateQuery(request, this.getConversionEngine());
		} catch (Exception e) {
			logger.error("Rethrowing as SRU exception:" + e);
			throw new SRUException(SRUConstants.SRU_UNSUPPORTED_PARAMETER,
					"Error during translation of query from UD to corpus-specific tagset. The query execution failed by this CLARIN-FCS (Blacklab Server) endpoint: " + e.getMessage());
		}
		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request, "nederlab");

		// instantiate the Nederlab query
		// fcs begint bij 1 te tellen, nederlab bij 0 (?)
		NederlabQuery nederlabQuery = new NederlabQuery(servletContext, this.getServer(), fcsContextCorpus, cqpQuery,
				request.getStartRecord() - 1, request.getMaximumRecords(), this.restrictTotalNumberOfResults, this.nederlabQueryTemplate,
				this.nederlabDocumentQueryTemplate, this.getEngineNativeUrlTemplate(),
				this.nederlabExtraResponseFields);

		// Log to Plausible server
		HttpServletRequest servletRequest = request.getServletRequest();
		logCorpusQuery(servletRequest, fcsContextCorpus, cqpQuery);

		// start the search and get the results

		try {
			ResultSet fcsResultSet = nederlabQuery.execute();

			// translate the results POS back into universal dependencies

			this.getConversionEngine().translateIntoUniversalDependencies(fcsResultSet);

			return new FcsSearchResultSet(config, request, diagnostics, fcsResultSet);

		} catch (Exception e) {
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"Error during execution of query or back-translation to UD. The query execution failed by this CLARIN-FCS (nederlab) endpoint. " + nederlabQuery + "\n" + e);
		}
	}

	// ---------------------------------------------------------------------------------
}
