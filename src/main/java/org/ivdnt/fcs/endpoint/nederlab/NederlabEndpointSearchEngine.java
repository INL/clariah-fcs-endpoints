package org.ivdnt.fcs.endpoint.nederlab;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.ivdnt.fcs.endpoint.bls.BlacklabSRUSearchResultSet;
import org.ivdnt.fcs.endpoint.bls.BlacklabServerEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.bls.BlacklabServerQuery;

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

public class NederlabEndpointSearchEngine extends KorpEndpointSearchEngine 
{
	
	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException 
	{
		String query = BlacklabServerEndpointSearchEngine.translateQuery(request);

		boolean hasFcsContextCorpus = false;
		
		String fcsContextCorpus = BlacklabServerQuery.defaultCorpus;
		
		for (String erd : request.getExtraRequestDataNames()) {
			if ("x-fcs-context".equals(erd)) {
				hasFcsContextCorpus = true;
				fcsContextCorpus = request.getExtraRequestData("x-fcs-context");
				break;
			}
		}
		
		

		NederlabQuery bq = new NederlabQuery(NederlabQuery.defaultServer, fcsContextCorpus, query);

		bq.startPosition = request.getStartRecord();
		bq.maximumResults = request.getMaximumRecords();

		try {
			clariah.fcs.ResultSet bsrs = bq.execute();

			return new BlacklabSRUSearchResultSet(config, request, diagnostics, bsrs);
		} catch (Exception e) {
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"The query execution failed by this CLARIN-FCS (nederlab) endpoint. " + bq);
		}

	}	
}
