package org.ivdnt.fcs.endpoint.corpusdependent;

import java.io.File;
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

import org.ivdnt.fcs.endpoint.bls.BlacklabServerEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.nederlab.NederlabEndpointSearchEngine;
import org.ivdnt.util.FileUtils;
import org.ivdnt.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import clariah.fcs.mapping.Conversion;
import clariah.fcs.mapping.ConversionTable;
import clariah.fcs.mapping.Conversions;
import clariah.fcs.mapping.JsonConversionObject;
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
 * @author jesse
 *
 *Choose implementation, determined by corpus
 *TODO: move configuration to a resource description file
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
	   
	   // Beware: The method must be synchronized, otherwise a first call involving
	   // ------  more than one engine would cause the engines the be initialized 
	   //         in each thread, which malfunction as a consequence. One single
	   //         initialisation in the first thread is enough.
	   
	   
	   // FIRST CALL:
	   // ----------
	   // fill tag sets conversion maps
	   
	   if ( (Conversions.getConversionTables()).size() == 0)
	   {
		   System.err.println( ">> loading tagsets conversion tables...");
		   
		   fillTagSetsConversionMap();
		   
		   System.err.println( ">> " + (Conversions.getConversionTables()).size() + 
				   " tagsets conversion tables loaded");
	   }
	   
	   // fill engine map
	   
	   if (engineMap.size() == 0)
	   {
		   System.err.println( ">> loading engines...");
		   
		   fillEngineMap();	
		   
		   System.err.println( ">> " + engineMap.size() + 
				   " engines loaded");
	   }
	   
	   // ----------
	   
	   for (String k: engineMap.keySet())
		   if (corpusId.toLowerCase().contains(k.toLowerCase())) 
		   {
			   System.err.printf("Choosing %s for %s\n", engineMap.get(k), corpusId);
			   return engineMap.get(k);
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
   
   
/** 
 * read the list of tag sets conversion tables and read each tag set conversion table
 */
   private void fillTagSetsConversionMap() {
	   
	   String enginesListFileName = "/WEB-INF/endpoint-engines-list.xml";
	   
	   URL url = null;
		try {
			url = contextCache.getResource(enginesListFileName);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
	   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	   dbf.setNamespaceAware(true);
	   dbf.setCoalescing(true);
	   
	   DocumentBuilder db;
	   Document doc;
	   
	   try {
			
			db = dbf.newDocumentBuilder();
			doc = db.parse(url.openStream());
			
			
			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();		    
		    
		    // parse
		    
		    XPathExpression enginesExpr;
		    NodeList enginesList;
		    
			try {				
				enginesExpr = xpath.compile("/Engines/Engine");
				enginesList = (NodeList) enginesExpr.evaluate(doc, XPathConstants.NODESET);				
				
				// process each engine
				
				HashSet<String> listOfLoadedConversions = new HashSet<String>();
				
				for (int engineNr = 0; engineNr < enginesList.getLength(); engineNr++)
				{
					Node oneEngine = enginesList.item(engineNr);	
					
					XPathExpression conversionNameExpr = xpath.compile(".//tagset-conversion-table");							
					String conversionName = conversionNameExpr.evaluate(oneEngine);					
					
					// load the tag set conversion into memory
					// 
					// NB: since a tagset conversion table might be in use by several engines
					//     it is necessary to check if the conversion table hasn't been loaded
					//     already!
					
					if ( !listOfLoadedConversions.contains(conversionName) )
					{
						listOfLoadedConversions.add(conversionName);
						readConversionMap(conversionName);
					}
										
				}
				
				
			} catch (XPathExpressionException e) {						
				e.printStackTrace();
			}			
							    
			
		} catch (ParserConfigurationException | SAXException | IOException e) {				
			throw new RuntimeException("Error while reading and parsing "+enginesListFileName, e);
		}
	  
   }
   
   /**
    *  read a tag set conversion map, give its name
    * @param name
    */
   private void readConversionMap(String name) {
	   
	   System.err.println("reading tagset "+name);
	   
	   String endConversionFileName = "/WEB-INF/"+name+".conversion.json";
	   
	   URL url = null;
		try {
			url = contextCache.getResource(endConversionFileName);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	   
	   ObjectMapper mapper = new ObjectMapper();
	   
	   try {
		   
		  // read the JSON file 
			JsonConversionObject oneConversion = mapper.readValue(url, JsonConversionObject.class);
			
			
			// DON'T REMOVE: convenient when debugging
			// -----------
			//String prettyConversion = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(oneConversion);
			//System.err.println(prettyConversion);
			
			// convert the data into the right format			 
			
			Conversions.processConversionTable(name, oneConversion);
		
		
	} catch (JsonParseException e) {
		Utils.printStackTrace(e);
	} catch (JsonMappingException e) {
		Utils.printStackTrace(e);
	} catch (IOException e) {
		Utils.printStackTrace(e);
	}
			
	   
   }
   
   /**
    *  read and process the list of engines 
    */
   private void fillEngineMap() {
	   
	   String endPointEngineListFileName = "/WEB-INF/endpoint-engines-list.xml";
	   
	   URL url = null;
	   
		try {
			url = this.contextCache.getResource(endPointEngineListFileName);
			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	   
	   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	   dbf.setNamespaceAware(true);
	   dbf.setCoalescing(true);
	   
	   DocumentBuilder db;
	   Document doc;
	   
		try {
			
			db = dbf.newDocumentBuilder();
			doc = db.parse(url.openStream());
			
			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();		    
		    
		    // parse
		    
		    XPathExpression engineExpr;
		    NodeList engineList;
		    
			try {
				engineExpr = xpath.compile("/Engines/Engine");
				engineList = (NodeList) engineExpr.evaluate(doc, XPathConstants.NODESET);				
				
				// process each engine
				
				for (int engineNr = 0; engineNr< engineList.getLength(); engineNr++)
				{
					Node oneEngine = engineList.item(engineNr);	
					
					XPathExpression engineNameExpr = xpath.compile(".//engine-name");
					XPathExpression engineTypeExpr = xpath.compile(".//engine-type");
					XPathExpression engineClassExpr = xpath.compile(".//engine-url");
					XPathExpression tagSetConversionTableExpr = xpath.compile(".//tagset-conversion-table");
					
					
					String engineName = engineNameExpr.evaluate(oneEngine);
					String engineType = engineTypeExpr.evaluate(oneEngine);
					String engineUrl = engineClassExpr.evaluate(oneEngine);
					String tagSetConversionTable = tagSetConversionTableExpr.evaluate(oneEngine);
					
					System.err.println("building "+engineName+" engine with "+tagSetConversionTable+" conversion table");
					
					
					Conversion conversionTable = Conversions.getConversionTable( tagSetConversionTable );
					
					
					// Nederlab engine type
					
					if (engineType.contains("nederlab"))
					{
						engineMap.put(engineName, new NederlabEndpointSearchEngine( engineUrl, conversionTable ));
					}
					
					// Blacklab engine type
					
					else
					{
						engineMap.put(engineName, new BlacklabServerEndpointSearchEngine( engineUrl, conversionTable ));
					}
					
				}
				
				
			} catch (XPathExpressionException e) {
				Utils.printStackTrace(e);
			}			
					    
			
		} catch (ParserConfigurationException | SAXException | IOException e) {				
			throw new RuntimeException("Error while parsing and parsing " + endPointEngineListFileName, e);
		}
   }
   


}
