package org.ivdnt.fcs.endpoint.corpusdependent;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;

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

import clariah.fcs.mapping.ConversionEngine;
import clariah.fcs.mapping.ConversionObject;
import clariah.fcs.mapping.ConversionObjectProcessor;
import eu.clarin.sru.server.fcs.SimpleEndpointSearchEngineBase;

public class CorpusDependentEngineFactory {
	
	ServletContext contextCache;
	
	
	/**
	 * Constructor
	 * 
	 * @param contextCache
	 */
	public CorpusDependentEngineFactory(ServletContext contextCache) {
		
		this.contextCache = contextCache; 
	}
	
	// --------------------------------------------------------------------------------
	
	
	
	/**
    *  read and process the list of engines 
    */
   public void fillEngineMap(Map<String, SimpleEndpointSearchEngineBase> engineMap) {
	   
	   URL url = null;
	   DocumentBuilder db;
	   Document doc;
	   
	   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	   dbf.setNamespaceAware(true);
	   dbf.setCoalescing(true);
	   	   
	   String endPointEngineListFileName = "endpoint-engines-list.xml";	   
	   
	   
	   /**
	    * by default, try to read config from 'config' directory
	    */
	   
	   try {
			   
		   doc = new FileUtils().readConfigDoc(this.contextCache, endPointEngineListFileName);
		   
		   parseAndFillEngineMap(doc, engineMap);
			
		   System.err.println("[Engine map] endpoint-engines-list.xml read from CONFIG DIR");
		   
		   
		   /**
		    * if this reading from 'config' directory fails,
		   // try to read from the resource folder
		    */
		   
		} catch (IOException | ParserConfigurationException | SAXException e2) {
			
			try {
				String endPointEngineListDefaultPath = File.separator + "WEB-INF" ;
				String endPointEngineListFilePath = endPointEngineListDefaultPath + File.separator + endPointEngineListFileName;
				
				url = this.contextCache.getResource(endPointEngineListFilePath);
				
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			
			try {
				
				db = dbf.newDocumentBuilder();
				doc = db.parse(url.openStream());
				
				parseAndFillEngineMap(doc, engineMap);
				
				System.err.println("[Engine map] endpoint-engines-list.xml read from WEB-INF");
						    
				
			} catch (ParserConfigurationException | SAXException | IOException e) {				
				throw new RuntimeException("Error while parsing and parsing " + endPointEngineListFileName, e);
			}
			
			
		}
	   
		
   }
   
   
   
   /**
    * Subroutine of fillEngineMap
    * 
    * @param doc
    * @param engineMap
    */
   private void parseAndFillEngineMap(Document doc, Map<String, SimpleEndpointSearchEngineBase> engineMap) {
	   
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
			
			
			ConversionEngine conversionEngine = ConversionObjectProcessor.getConversionEngine( tagSetConversionTable );
			
			
			// Nederlab engine type
			
			if (engineType.contains("nederlab"))
			{
				engineMap.put(engineName, new NederlabEndpointSearchEngine( engineUrl, conversionEngine ));
			}
			
			// Blacklab engine type
			
			else
			{
				engineMap.put(engineName, new BlacklabServerEndpointSearchEngine( engineUrl, conversionEngine ));
			}
			
		}
		
		
	} catch (XPathExpressionException e) {
		Utils.printStackTrace(e);
	}
	
	
   }
   

	// --------------------------------------------------------------------------------
	
	/** 
	 * read the list of tag sets conversion tables and read each tag set conversion table
	 */
	public void fillTagSetsConversionMap() {		
		
		URL url = null;
		DocumentBuilder db;
		Document doc;
	   
	   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	   dbf.setNamespaceAware(true);
	   dbf.setCoalescing(true);
	   	   
	   String endPointEngineListFileName = "endpoint-engines-list.xml";	   
	   
	   
	   /**
	    * by default, try to read config from 'config' directory
	    */
	   
	   try {
			   
		   doc = new FileUtils().readConfigDoc(this.contextCache, endPointEngineListFileName);
		   
		   parseAndfillTagSetsConversionMap(doc);
			
		   System.err.println("[Tagset conversion map] endpoint-engines-list.xml read from CONFIG DIR");
		   
		   
		   /**
		    * if this reading from 'config' directory fails,
		   // try to read from the resource folder
		    */
		   
		} 
	   catch (IOException | ParserConfigurationException | SAXException e2) {			
			
			
			try {
				String endPointEngineListDefaultPath = File.separator + "WEB-INF" ;
				String endPointEngineListFilePath = endPointEngineListDefaultPath + File.separator + endPointEngineListFileName;
				
				url = this.contextCache.getResource(endPointEngineListFilePath);
				
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			
			try {
				
				db = dbf.newDocumentBuilder();
				doc = db.parse(url.openStream());
				
				parseAndfillTagSetsConversionMap(doc);
				
				System.err.println("[Tagset conversion map] endpoint-engines-list.xml read from WEB-INF");
						    
				
			} catch (ParserConfigurationException | SAXException | IOException e) {				
				throw new RuntimeException("Error while parsing and parsing " + endPointEngineListFileName, e);
			}
			
			
		}
	   catch ( Exception e) {				
			throw new RuntimeException("Error while reading and parsing "+endPointEngineListFileName, e);
		}

		  
	   }


	/**
	 * subroutine of fillTagSetsConversionMap
	 * 
	 * @param doc
	 */
	private void parseAndfillTagSetsConversionMap(Document doc) {
		
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
	}
	   
	
	
	   /**
	    *  read a tag set conversion map, give its name
	    * @param name
	    */
	   private void readConversionMap(String name) {
		   
		   System.err.println("reading tagset "+name);
		   
		   String endConversionFileName = name + ".conversion.json";
		   
		   
		   // we'll need to map the json onto a java object
		   ObjectMapper mapper = new ObjectMapper();		   
		   
		   
		   try {
			   
			String conversionString = new FileUtils().readConfigFile(this.contextCache, endConversionFileName);
			
			// read the JSON file 
			
			ConversionObject oneConversion = mapper.readValue(conversionString, ConversionObject.class);
			
			// convert the data into the right format			 
			
			ConversionObjectProcessor.processConversionTable(name, oneConversion);
			
			System.err.println("[Tagset conversion set] "+endConversionFileName+" read from CONFIG DIR");
			
			
			
			} catch (IOException e2) {
			
				 String endConversionFilePath = File.separator + "WEB-INF" + File.separator + endConversionFileName;
				   
				   URL url = null;
					try {
						url = contextCache.getResource(endConversionFilePath);
						
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
				   
				   
				   
				   try {
					   
					   // read the JSON file 
					   ConversionObject oneConversion = mapper.readValue(url, ConversionObject.class);						
						
						// convert the data into the right format			 
						
						ConversionObjectProcessor.processConversionTable(name, oneConversion);
						
						System.err.println("[Tagset conversion set] "+endConversionFileName+" read from WEB-INF");
					
					
					} catch (JsonParseException e) {
						Utils.printStackTrace(e);
					} catch (JsonMappingException e) {
						Utils.printStackTrace(e);
					} catch (IOException e) {
						Utils.printStackTrace(e);
					}
			
			}
		   
		   
		   
		  
				
		   
	   }
	   
}
