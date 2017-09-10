package org.ivdnt.fcs.endpoint.corpusdependent;

import eu.clarin.sru.server.CQLQueryParser;
import eu.clarin.sru.server.SRUConfigException;
import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRUQueryParserRegistry;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;
import eu.clarin.sru.server.fcs.*;
import se.gu.spraakbanken.fcs.endpoint.korp.KorpEndpointSearchEngine;
import se.gu.spraakbanken.fcs.endpoint.korp.KorpSRUSearchResultSet;
import se.gu.spraakbanken.fcs.endpoint.korp.cqp.FCSToCQPConverter;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query.Query;
import java.util.*;

import javax.servlet.ServletContext;

import org.ivdnt.fcs.endpoint.bls.BlacklabServerEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.bls.BlacklabServerQuery;
import org.ivdnt.fcs.endpoint.nederlab.NederlabEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.nederlab.NederlabQuery;

import clariah.fcs.mapping.Conversions;

public class CorpusDependentEngine extends se.gu.spraakbanken.fcs.endpoint.korp.KorpEndpointSearchEngine 
{
   SimpleEndpointSearchEngineBase engine;
   
   // TODO: server may also depend on corpus identifier
   // The corpus id also may need to be mapped
   
   Map<String, SimpleEndpointSearchEngineBase> engineMap = new HashMap<String, SimpleEndpointSearchEngineBase>() 
   { 
	   {
		   this.put("nederlab",  new NederlabEndpointSearchEngine(NederlabQuery.defaultServer, Conversions.UD2CGNNederlab));
		   this.put("opensonar",  new BlacklabServerEndpointSearchEngine(BlacklabServerQuery.defaultServer, Conversions.UD2CGNSonar));
		   
		   this.put("chn",  new BlacklabServerEndpointSearchEngine("http://chn-i.inl.nl/blacklab-server/"));
		   this.put("zeebrieven", new BlacklabServerEndpointSearchEngine("http://svprre02.inl.loc:8080/blacklab-server/")); 
		   this.put("StatenGeneraal", new BlacklabServerEndpointSearchEngine("http://svprre02.inl.loc:8080/blacklab-server/", Conversions.UD2CHN)); 
	   }
   };
   
   private SimpleEndpointSearchEngineBase chooseEngine(String corpusId)
   {
	   final SimpleEndpointSearchEngineBase engine = null;
	   for (String k: engineMap.keySet())
		   if (corpusId.toLowerCase().contains(k.toLowerCase())) 
		   {
			   System.err.printf("Choosing %s for %s\n", engineMap.get(k), corpusId);
			   return engineMap.get(k);
		   }
	   
	   System.err.println("Could not find engine for corpus: " + corpusId + ", send to korp");
	   return new se.gu.spraakbanken.fcs.endpoint.korp.KorpEndpointSearchEngine();
   }
   
   protected void doInit(ServletContext context, SRUServerConfig config,
			SRUQueryParserRegistry.Builder queryParserBuilder, Map<String, String> params) throws SRUConfigException 
   {
		super.doInit(context, config, queryParserBuilder, params);
		// engineMap.forEach((k,e)->e.doInit(context, config, queryParserBuilder, params));
   }
   
   public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException {
		
		boolean hasFcsContextCorpus = false;
		String fcsContextCorpus = "opensonar";
		for (String erd : request.getExtraRequestDataNames()) {
			if ("x-fcs-context".equals(erd)) {
				hasFcsContextCorpus = true;
				fcsContextCorpus = request.getExtraRequestData("x-fcs-context");
				break;
			}
		}
		SimpleEndpointSearchEngineBase engine = chooseEngine(fcsContextCorpus);
		return engine.search(config,request,diagnostics);
	}

}