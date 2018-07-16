package org.ivdnt.fcs.endpoint.corpusdependent;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.ivdnt.fcs.endpoint.blacklab.BlacklabServerEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.nederlab.NederlabEndpointSearchEngine;
import org.ivdnt.fcs.mapping.ConversionEngine;
import org.ivdnt.fcs.mapping.ConversionObject;
import org.ivdnt.fcs.mapping.ConversionObjectProcessor;
import org.ivdnt.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.clarin.sru.server.fcs.SimpleEndpointSearchEngineBase;

public class CorpusDependentEngineBuilder {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	ServletContext contextCache;

	/**
	 * Constructor
	 * 
	 * @param contextCache
	 */
	public CorpusDependentEngineBuilder(ServletContext contextCache) {

		this.contextCache = contextCache;
	}

	// --------------------------------------------------------------------------------

	/**
	 * read and process the list of engines
	 */
	public void fillEngineMap(Map<String, SimpleEndpointSearchEngineBase> engineMap) {

		String endPointEngineListFileName = "endpoint-engines-list.xml";

		Document doc = new FileUtils(this.contextCache, endPointEngineListFileName).readConfigFileAsDoc();
		parseAndFillEngineMap(doc, engineMap);
	}

	/**
	 * Subroutine of fillEngineMap
	 * 
	 * @param doc
	 * @param engineMap
	 */
	private void parseAndFillEngineMap(Document doc, Map<String, SimpleEndpointSearchEngineBase> engineMap) {

		// optional, but recommended
		// read this -
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		// parse

		XPathExpression engineExpr;
		NodeList engineList;

		try {
			engineExpr = xpath.compile("/Engines/Engine");
			engineList = (NodeList) engineExpr.evaluate(doc, XPathConstants.NODESET);

			// First, make pass through engines to generate list of conversion tables
			// process each engine

			HashSet<String> listOfLoadedConversions = new HashSet<String>();

			// process each engine

			for (int engineNr = 0; engineNr < engineList.getLength(); engineNr++) {
				Node oneEngine = engineList.item(engineNr);

				XPathExpression engineNameExpr = xpath.compile(".//engine-name");
				XPathExpression engineTypeExpr = xpath.compile(".//engine-type");
				XPathExpression engineClassExpr = xpath.compile(".//engine-url");
				XPathExpression engineNativeUrlTemplateExpr = xpath.compile(".//engine-native-url-template");
				XPathExpression tagSetConversionTableExpr = xpath.compile(".//tagset-conversion-table");

				String engineName = engineNameExpr.evaluate(oneEngine);
				String engineType = engineTypeExpr.evaluate(oneEngine);
				String engineUrl = engineClassExpr.evaluate(oneEngine);
				String engineNativeUrlTemplate = engineNativeUrlTemplateExpr.evaluate(oneEngine);
				String tagSetConversionTable = tagSetConversionTableExpr.evaluate(oneEngine);

				logger.info("building " + engineName + " engine with " + tagSetConversionTable + " conversion table");

				// load the tag set conversion into memory
				//
				// NB: since a tagset conversion table might be in use by several engines
				// it is necessary to check if the conversion table hasn't been loaded
				// already!
				if (!listOfLoadedConversions.contains(tagSetConversionTable)) {
					listOfLoadedConversions.add(tagSetConversionTable);
					readConversionMap(tagSetConversionTable);
				}
				// Now get conversion engine, which may just have been loaded
				ConversionEngine conversionEngine = ConversionObjectProcessor
						.getConversionEngine(tagSetConversionTable);

				// Nederlab engine type

				if (engineType.contains("nederlab")) {
					// For NederLab, parse extra response fields from config file
					XPathExpression nederlabExtraResponseFieldsExpr = xpath
							.compile(".//nederlab-extra-response-fields/field");
					NodeList nederlabExtraResponseFieldsNl = (NodeList) nederlabExtraResponseFieldsExpr
							.evaluate(oneEngine, XPathConstants.NODESET);
					List<String> nederlabExtraResponseFields = new ArrayList<String>();
					for (int i = 0; i < nederlabExtraResponseFieldsNl.getLength(); i++) {
						String value = nederlabExtraResponseFieldsNl.item(i).getTextContent();
						nederlabExtraResponseFields.add(value);
					}
					// Get query template location from conf
					XPathExpression nederlabQueryTemplateLocationExpr = xpath
							.compile(".//nederlab-query-template-location");
					XPathExpression nederlabDocumentQueryTemplateLocationExpr = xpath
							.compile(".//nederlab-document-query-template-location");
					String nederlabQueryTemplateLocation = nederlabQueryTemplateLocationExpr.evaluate(oneEngine);
					String nederlabDocumentQueryTemplateLocation = nederlabDocumentQueryTemplateLocationExpr
							.evaluate(oneEngine);
					String queryTemplate = readQueryTemplate(nederlabQueryTemplateLocation);
					String documentQueryTemplate = readQueryTemplate(nederlabDocumentQueryTemplateLocation);
					engineMap.put(engineName,
							new NederlabEndpointSearchEngine(contextCache, engineUrl, conversionEngine, queryTemplate,
									documentQueryTemplate, engineNativeUrlTemplate, nederlabExtraResponseFields));
				}

				// Blacklab engine type

				else {
					engineMap.put(engineName, new BlacklabServerEndpointSearchEngine(engineUrl, conversionEngine,
							engineNativeUrlTemplate));
				}

			}

		} catch (XPathExpressionException e) {
			throw new RuntimeException("Exception processing engine map.", e);
		}

	}

	// --------------------------------------------------------------------------------

	/**
	 * read a tag set conversion map, give its name
	 * 
	 * @param name
	 */
	private void readConversionMap(String name) {

		logger.info("reading tagset " + name);

		String endConversionFileName = name + ".conversion.json";

		// we'll need to map the json onto a java object
		ObjectMapper mapper = new ObjectMapper();
		ConversionObject oneConversion = null;

		String conversionString = new FileUtils(this.contextCache, endConversionFileName).readConfigFileAsString();
		// read the JSON file
		try {
			oneConversion = mapper.readValue(conversionString, ConversionObject.class);
		} catch (IOException e) {
			throw new RuntimeException("Exception while reading conversion map: " + name, e);
		}

		// convert the data into the right format
		ConversionObjectProcessor.processConversionTable(name, oneConversion);

	}

	// //
	// --------------------------------------------------------------------------------
	//
	// /**
	// * read the list of tag sets conversion tables and read each tag set
	// conversion
	// * table
	// */
	// public void fillTagSetsConversionMap() {
	//
	// URL url = null;
	// DocumentBuilder db;
	// Document doc;
	//
	// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	// dbf.setNamespaceAware(true);
	// dbf.setCoalescing(true);
	//
	// String endPointEngineListFileName = "endpoint-engines-list.xml";
	//
	// /**
	// * by default, try to read config from 'config' directory
	// */
	//
	// try {
	//
	// doc = new FileUtils(this.contextCache,
	// endPointEngineListFileName).readConfigFileAsDoc();
	//
	// parseAndfillTagSetsConversionMap(doc);
	//
	// System.err.println("[Tagset conversion map] endpoint-engines-list.xml read
	// from CONFIG DIR");
	//
	// /**
	// * if this reading from 'config' directory fails, // try to read from the
	// * resource folder
	// */
	//
	// } catch (IOException | ParserConfigurationException | SAXException e2) {
	//
	// try {
	// String endPointEngineListDefaultPath = File.separator + "WEB-INF";
	// String endPointEngineListFilePath = endPointEngineListDefaultPath +
	// File.separator
	// + endPointEngineListFileName;
	//
	// url = this.contextCache.getResource(endPointEngineListFilePath);
	//
	// } catch (MalformedURLException e1) {
	// e1.printStackTrace();
	// }
	//
	// try {
	//
	// db = dbf.newDocumentBuilder();
	// doc = db.parse(url.openStream());
	//
	// parseAndfillTagSetsConversionMap(doc);
	//
	// System.err.println("[Tagset conversion map] endpoint-engines-list.xml read
	// from WEB-INF");
	//
	// } catch (ParserConfigurationException | SAXException | IOException e) {
	// throw new RuntimeException("Error while parsing and parsing " +
	// endPointEngineListFileName, e);
	// }
	//
	// } catch (Exception e) {
	// throw new RuntimeException("Error while reading and parsing " +
	// endPointEngineListFileName, e);
	// }
	//
	// }
	//
	// /**
	// * subroutine of fillTagSetsConversionMap
	// *
	// * @param doc
	// */
	// private void parseAndfillTagSetsConversionMap(Document doc) {
	//
	// // optional, but recommended
	// // read this -
	// //
	// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	// doc.getDocumentElement().normalize();
	//
	// XPathFactory factory = XPathFactory.newInstance();
	// XPath xpath = factory.newXPath();
	//
	// // parse
	//
	// XPathExpression enginesExpr;
	// NodeList enginesList;
	//
	// try {
	// enginesExpr = xpath.compile("/Engines/Engine");
	// enginesList = (NodeList) enginesExpr.evaluate(doc, XPathConstants.NODESET);
	//
	// // process each engine
	//
	// HashSet<String> listOfLoadedConversions = new HashSet<String>();
	//
	// for (int engineNr = 0; engineNr < enginesList.getLength(); engineNr++) {
	// Node oneEngine = enginesList.item(engineNr);
	//
	// XPathExpression conversionNameExpr =
	// xpath.compile(".//tagset-conversion-table");
	// String conversionName = conversionNameExpr.evaluate(oneEngine);
	//
	// // load the tag set conversion into memory
	// //
	// // NB: since a tagset conversion table might be in use by several engines
	// // it is necessary to check if the conversion table hasn't been loaded
	// // already!
	//
	// if (!listOfLoadedConversions.contains(conversionName)) {
	// listOfLoadedConversions.add(conversionName);
	// readConversionMap(conversionName);
	// }
	//
	// }
	//
	// } catch (XPathExpressionException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Read the Nederlab query template
	 * 
	 * @param filename
	 * @return
	 */
	public String readQueryTemplate(String filename) {

		String queryTemplate = new FileUtils(this.contextCache, filename).readConfigFileAsString();

		return queryTemplate;
	}

}
