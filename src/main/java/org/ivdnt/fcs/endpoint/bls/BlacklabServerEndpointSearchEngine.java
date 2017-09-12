package org.ivdnt.fcs.endpoint.bls;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import clariah.fcs.mapping.Conversion;
import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;


public class BlacklabServerEndpointSearchEngine  extends BasicEndpointSearchEngine
{
	String server = BlacklabServerQuery.defaultServer;
	clariah.fcs.mapping.Conversion conversion = null;
	
	public BlacklabServerEndpointSearchEngine()
	{
		super();
	}
	
	public BlacklabServerEndpointSearchEngine(String server)
	{
		super();
		this.server = server;
	}
	
	public BlacklabServerEndpointSearchEngine(String server, Conversion conversion)
	{
		super();
		this.server = server;
		this.conversion = conversion;
	}
	
	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException 
	{
		String query = BasicEndpointSearchEngine.translateQuery(request, conversion);

		boolean hasFcsContextCorpus = false;
		
		String fcsContextCorpus = BlacklabServerQuery.defaultCorpus;
		
		for (String erd : request.getExtraRequestDataNames()) {
			if ("x-fcs-context".equals(erd)) {
				hasFcsContextCorpus = true;
				fcsContextCorpus = request.getExtraRequestData("x-fcs-context"); // TODO fix this in corpusinfo implementation
				break;
			}
		}
		
		
		BlacklabServerQuery bq = new BlacklabServerQuery(this.server, fcsContextCorpus, query);

		bq.startPosition = request.getStartRecord()-1; // bij fcs beginnen ze bij 1 te tellen ?
		bq.maximumResults = request.getMaximumRecords();
		System.err.println("Query to blacklab server: " + bq);
		try {
			BlacklabServerResultSet bsrs = bq.execute();

			return new BlacklabSRUSearchResultSet(config, request, diagnostics, bsrs);
		} catch (Exception e) {
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
					"The query execution failed by this CLARIN-FCS (Blacklab Server) endpoint: " + e.getMessage() +  "; Query: " + bq.toString());
		}
		

	}
}
