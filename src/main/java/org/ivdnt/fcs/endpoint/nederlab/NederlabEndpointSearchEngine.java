package org.ivdnt.fcs.endpoint.nederlab;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.ivdnt.fcs.endpoint.bls.BlacklabSRUSearchResultSet;
import org.ivdnt.fcs.endpoint.bls.BlacklabServerQuery;
import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import clariah.fcs.mapping.Conversion;
import eu.clarin.sru.server.CQLQueryParser;
import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;
import eu.clarin.sru.server.fcs.Constants;
import eu.clarin.sru.server.fcs.FCSQueryParser;


public class NederlabEndpointSearchEngine  extends BasicEndpointSearchEngine
{
	
	String server = NederlabQuery.defaultServer;
	clariah.fcs.mapping.Conversion conversion = null;
	
	public NederlabEndpointSearchEngine(String server, Conversion conversion)
	{
		super();
		this.server = server;
		this.conversion = conversion;
	}
	
	public NederlabEndpointSearchEngine() {
		// TODO Auto-generated constructor stub
		super();
	}

	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException 
	{
		String query = BasicEndpointSearchEngine.translateQuery(request,conversion);

		boolean hasFcsContextCorpus = false;
		
		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request, "nederlab");
		
		NederlabQuery bq = new NederlabQuery(server, fcsContextCorpus, query);

		bq.startPosition = request.getStartRecord()-1; // fcs begint bij 1 te tellen, nederlab bij 0 (?)
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
