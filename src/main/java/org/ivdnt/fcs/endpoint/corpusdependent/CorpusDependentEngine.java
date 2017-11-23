package org.ivdnt.fcs.endpoint.corpusdependent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.ivdnt.fcs.endpoint.blacklab.BlacklabServerEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.nederlab.NederlabEndpointSearchEngine;
import org.ivdnt.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import clariah.fcs.mapping.ConversionEngine;
import clariah.fcs.mapping.ConversionObjectProcessor;
import clariah.fcs.mapping.ConversionObject;
import eu.clarin.sru.server.SRUConfigException;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRUQueryParserRegistry;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;
import eu.clarin.sru.server.fcs.SimpleEndpointSearchEngineBase;
/**
 * 
 * @author jesse, mathieu
 *
 * Choose implementation, determined by corpus
 *
 */
public class CorpusDependentEngine extends BasicEndpointSearchEngine
{
   SimpleEndpointSearchEngineBase engine;
   ServletContext contextCache;
   
   // TODO: server may also depend on corpus identifier
   // The corpus id also may need to be mapped
   
   Map<String, SimpleEndpointSearchEngineBase> engineMap = new ConcurrentHashMap<String, SimpleEndpointSearchEngineBase>(); 


   /**
    * Load and choose an Engine, upon a search request
    * @param corpusId
    * @return
    */
   private synchronized SimpleEndpointSearchEngineBase chooseEngine(String corpusId)
   {
	   
	   CorpusDependentEngineFactory engineFactory = 
			   new CorpusDependentEngineFactory( this.contextCache );
	   
	   // Beware: This method must be synchronized, otherwise a first call involving
	   // ------  more than one engine would cause the engines the be initialized 
	   //         in each thread, which malfunction as a consequence. One single
	   //         initialisation in the very first thread is enough.
	   
	   
	   // FIRST CALL:
	   // ----------
	   // fill tag sets conversion maps
	   
	   if ( (ConversionObjectProcessor.getConversionEngines()).size() == 0 )
	   {
		   System.err.println( ">> loading tagsets conversion tables...");
		   
		   engineFactory.fillTagSetsConversionMap();
		   
		   System.err.println( ">> " + (ConversionObjectProcessor.getConversionEngines()).size() + 
				   " tagsets conversion tables loaded");
	   }
	   
	   // fill engine map
	   
	   if (this.engineMap.size() == 0)
	   {
		   System.err.println( ">> loading engines...");
		   
		   engineFactory.fillEngineMap( this.engineMap );	
		   
		   System.err.println( ">> " + this.engineMap.size() + 
				   " engines loaded");
	   }
	   
	   // now pick up the engine we need
	   
	   for (String k: this.engineMap.keySet())
		   if (corpusId.toLowerCase().contains(k.toLowerCase())) 
		   {
			   System.err.printf("Choosing %s for %s\n", this.engineMap.get(k), corpusId);
			   return this.engineMap.get(k);
		   }
	   
	   System.err.println("Could not find engine for corpus: " + corpusId);
	   return null;
   }
   
   
   
   
   protected void doInit(ServletContext context, SRUServerConfig config,
			SRUQueryParserRegistry.Builder queryParserBuilder, Map<String, String> params) throws SRUConfigException 
   {
		this.contextCache = context;
		super.doInit(context, config, queryParserBuilder, params);
		// engineMap.forEach((k,e)->e.doInit(context, config, queryParserBuilder, params));
   }
   
   public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException {
	  
		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request, "opensonar");
		SimpleEndpointSearchEngineBase engine = chooseEngine(fcsContextCorpus);
		return engine.search(config, request, diagnostics);
	}
   
   

   


}
