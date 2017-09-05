package org.ivdnt.fcs.endpoint.bls;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.clarin.sru.server.CQLQueryParser;
import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;
import eu.clarin.sru.server.fcs.Constants;
import eu.clarin.sru.server.fcs.FCSQueryParser;
import se.gu.spraakbanken.fcs.endpoint.korp.KorpEndpointSearchEngine;
import se.gu.spraakbanken.fcs.endpoint.korp.cqp.FCSToCQPConverter;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info.CorporaInfo;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query.Query;

public class BlacklabServerEndpointSearchEngine extends KorpEndpointSearchEngine 
{
	
	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException 
	{
		String query = translateQuery(request);

		boolean hasFcsContextCorpus = false;
		
		String fcsContextCorpus = BlacklabServerQuery.defaultCorpus;
		
		for (String erd : request.getExtraRequestDataNames()) {
			if ("x-fcs-context".equals(erd)) {
				hasFcsContextCorpus = true;
				// fcsContextCorpus = request.getExtraRequestData("x-fcs-context"); // TODO fix this in corpusinfo implementation
				break;
			}
		}
		
		if (hasFcsContextCorpus && !"".equals(fcsContextCorpus)) {
			if (!"hdl%3A10794%2Fsbmoderna".equals(fcsContextCorpus)) {
				// LOG.info("Loading specific corpus data: '{}'", fcsContextCorpus);
				// getCorporaInfo();
			}
			// hdl%3A10794%2Fsbmoderna is the default
		}

		BlacklabServerQuery bq = new BlacklabServerQuery(BlacklabServerQuery.defaultServer, fcsContextCorpus, query);

		bq.startPosition = request.getStartRecord();
		bq.maximumResults = request.getMaximumRecords();

		try {
			BlacklabServerResultSet bsrs = bq.execute();

			return new BlacklabSRUSearchResultSet(config, request, diagnostics, bsrs);
		} catch (Exception e) {
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"The query execution failed by this CLARIN-FCS (Blacklab Server) Endpoint: " + e.getMessage() +  "; Query URL: " + bq.url());
		}
		/*
		 * Query queryRes = makeAndExecuteQuery(query, fcsContextCorpus,
		 * request.getStartRecord(), request.getMaximumRecords()); if (queryRes == null)
		 * { throw new
		 * SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
		 * "The query execution failed by this CLARIN-FCS Endpoint."); }
		 */

	}

	public static String translateQuery(SRURequest request) throws SRUException {
		String query;
	
		if (request.isQueryType(Constants.FCS_QUERY_TYPE_CQL)) {
			/*
			 * Got a CQL query (either SRU 1.1 or higher). Translate to a proper CQP query
			 * ...
			 */
			final CQLQueryParser.CQLQuery q = request.getQuery(CQLQueryParser.CQLQuery.class);
			query = FCSToCQPConverter.makeCQPFromCQL(q);
		} else if (request.isQueryType(Constants.FCS_QUERY_TYPE_FCS)) {
			/*
			 * Got a FCS query (SRU 2.0). Translate to a proper CQP query
			 */
			final FCSQueryParser.FCSQuery q = request.getQuery(FCSQueryParser.FCSQuery.class);
			query = FCSToCQPConverter.makeCQPFromFCS(q);
		} else {
			/*
			 * Got something else we don't support. Send error ...
			 */
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN, "Queries with queryType '"
					+ request.getQueryType() + "' are not supported by this CLARIN-FCS Endpoint.");
		}
		return query;
	}
}
