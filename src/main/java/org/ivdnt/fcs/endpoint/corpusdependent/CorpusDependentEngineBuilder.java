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
import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;
import org.ivdnt.fcs.endpoint.nederlab.NederlabEndpointSearchEngine;
import org.ivdnt.fcs.mapping.ConversionEngine;
import org.ivdnt.fcs.mapping.ConversionObject;
import org.ivdnt.fcs.mapping.ConversionObjectProcessor;
import org.ivdnt.util.Plausible;
import org.ivdnt.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CorpusDependentEngineBuilder {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private CorpusDependentEngineBuilder() {}

	// --------------------------------------------------------------------------------

	/**
	 * read and process the list of engines
	 * @param engineMap Maps engine names to engine implementation
	 */
	public static void fillEngineMap(Map<String, BasicEndpointSearchEngine> engineMap, ServletContext servletContext) {

		String endPointEngineListFileName = "endpoint-engines-list.xml";

		Document doc = new FileUtils(servletContext, endPointEngineListFileName).parseXml();

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
				XPathExpression restrictTotalNumberOfResultsExpr = xpath.compile(".//restrict-total-number-of-results");
				XPathExpression engineNativeUrlTemplateExpr = xpath.compile(".//engine-native-url-template");
				XPathExpression tagSetConversionTableExpr = xpath.compile(".//tagset-conversion-table");

				String engineName = engineNameExpr.evaluate(oneEngine);
				String engineType = engineTypeExpr.evaluate(oneEngine);
				String engineUrl = engineClassExpr.evaluate(oneEngine);
				int restrictTotalNumberOfResults = 0;
				try {
					restrictTotalNumberOfResults = Integer.parseInt(restrictTotalNumberOfResultsExpr.evaluate(oneEngine));
				}
				catch (NumberFormatException e) {
					logger.warn("No valid number given for restrict-total-number-of-results, defaulting to 0.");
				}
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
					readConversionMap(tagSetConversionTable, servletContext);
				}
				// Now get conversion engine, which may just have been loaded
				ConversionEngine conversionEngine = ConversionObjectProcessor
						.getConversionEngine(tagSetConversionTable);

				// Nederlab engine type

				BasicEndpointSearchEngine engine;
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
					String queryTemplate = readQueryTemplate(nederlabQueryTemplateLocation, servletContext);
					String documentQueryTemplate = readQueryTemplate(nederlabDocumentQueryTemplateLocation, servletContext);
					engine = new NederlabEndpointSearchEngine(servletContext, engineUrl,
							conversionEngine, restrictTotalNumberOfResults, queryTemplate,
							documentQueryTemplate, engineNativeUrlTemplate, nederlabExtraResponseFields);
				} else {
					// Blacklab engine type
					engine = new BlacklabServerEndpointSearchEngine(engineUrl,
							conversionEngine,
							restrictTotalNumberOfResults, engineNativeUrlTemplate);
				}
				engineMap.put(engineName, engine);
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
	private static void readConversionMap(String name, ServletContext servletContext) {

		logger.info("reading tagset " + name);

		String endConversionFileName = name + ".conversion.json";

		// we'll need to map the json onto a java object
		ObjectMapper mapper = new ObjectMapper();
		ConversionObject oneConversion = null;

		String conversionString = new FileUtils(servletContext, endConversionFileName).readToString();
		// read the JSON file
		try {
			oneConversion = mapper.readValue(conversionString, ConversionObject.class);
		} catch (IOException e) {
			throw new RuntimeException("Exception while reading conversion map: " + name, e);
		}

		// convert the data into the right format
		ConversionObjectProcessor.processConversionTable(name, oneConversion);
	}

	/**
	 * Read the Nederlab query template
	 * 
	 * @param filename file name to read template from
	 * @return query template as string
	 */
	private static String readQueryTemplate(String filename, ServletContext servletContext) {
        return new FileUtils(servletContext, filename).readToString();
	}

}
