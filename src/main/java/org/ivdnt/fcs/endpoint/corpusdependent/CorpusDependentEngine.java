package org.ivdnt.fcs.endpoint.corpusdependent;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.ivdnt.fcs.endpoint.common.BasicEndpointSearchEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author jesse, mathieu, peter
 *
 *         Choose implementation, determined by corpus
 *
 */
public class CorpusDependentEngine extends BasicEndpointSearchEngine {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	SimpleEndpointSearchEngineBase engine;
	ServletContext contextCache;

	Map<String, SimpleEndpointSearchEngineBase> engineMap = new ConcurrentHashMap<String, SimpleEndpointSearchEngineBase>();

	/**
	 * Load and choose an Engine, upon a search request
	 * 
	 * @param corpusId
	 * @return
	 */
	private synchronized SimpleEndpointSearchEngineBase chooseEngine(String corpusId) {

		CorpusDependentEngineBuilder enginebuilder = new CorpusDependentEngineBuilder(this.contextCache);

		// Beware: This method must be synchronized, otherwise a first call involving
		// ------ more than one engine would cause the engines the be initialized
		// in each thread, which malfunction as a consequence. One single
		// initialisation in the very first thread is enough.

		/*
		 * // FIRST CALL: // ---------- // fill tag sets conversion maps
		 * 
		 * if ((ConversionObjectProcessor.getConversionEngines()).size() == 0) {
		 * System.err.println(">> loading tagsets conversion tables...");
		 * 
		 * enginebuilder.fillTagSetsConversionMap();
		 * 
		 * System.err.println(">> " +
		 * (ConversionObjectProcessor.getConversionEngines()).size() +
		 * " tagsets conversion tables loaded"); }
		 */

		// fill engine map

		if (this.engineMap.size() == 0) {
			logger.info(">> loading engines...");

			enginebuilder.fillEngineMap(this.engineMap);

			logger.info(">> " + this.engineMap.size() + " engines loaded");
		}

		// now pick up the engine we need

		for (String k : this.engineMap.keySet())
			if (corpusId.toLowerCase().contains(k.toLowerCase())) {
				System.err.printf("Choosing %s for %s\n", this.engineMap.get(k), corpusId);
				return this.engineMap.get(k);
			}

		logger.error("Could not find engine for corpus: " + corpusId);
		return null;
	}

	protected void doInit(ServletContext context, SRUServerConfig config,
			SRUQueryParserRegistry.Builder queryParserBuilder, Map<String, String> params) throws SRUConfigException {
		this.contextCache = context;
		super.doInit(context, config, queryParserBuilder, params);
	}

	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException {

		String fcsContextCorpus = BasicEndpointSearchEngine.getCorpusNameFromRequest(request, "opensonar");
		SimpleEndpointSearchEngineBase engine = chooseEngine(fcsContextCorpus);
		return engine.search(config, request, diagnostics);
	}

}
