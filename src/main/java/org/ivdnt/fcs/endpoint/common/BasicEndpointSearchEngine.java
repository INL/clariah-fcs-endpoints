/**
 *
 * @license http://www.gnu.org/licenses/gpl-3.0.txt
 *  GNU General Public License v3
 */
package org.ivdnt.fcs.endpoint.common;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletContext;

import org.ivdnt.fcs.mapping.ConversionEngine;
import org.ivdnt.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.clarin.sru.server.CQLQueryParser;
import eu.clarin.sru.server.SRUConfigException;
import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRUQueryParserRegistry;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUScanResultSet;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;
import eu.clarin.sru.server.fcs.Constants;
import eu.clarin.sru.server.fcs.EndpointDescription;
import eu.clarin.sru.server.fcs.FCSQueryParser;
import eu.clarin.sru.server.fcs.SimpleEndpointSearchEngineBase;
import eu.clarin.sru.server.fcs.utils.SimpleEndpointDescriptionParser;

/**
 * Base class for endpoint search engines, mainly copies from the Korp reference
 * example
 * 
 * It implements some methods, which are abstract in the
 * SimpleEndpointSearchEngineBase but it extends this class as well.
 *
 */
public class BasicEndpointSearchEngine extends SimpleEndpointSearchEngineBase {
		
	protected int restrictTotalNumberOfResults;

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * New method (no part of SimpleEndpointSearchEngineBase)
	 * 
	 * Get the corpus name out of the request
	 * 
	 * @param request request
	 * @param defaultCorpus default corpus
	 * @return fcsContextCorpus
	 */
	public static String getCorpusNameFromRequest(SRURequest request, String defaultCorpus) {
		String fcsContextCorpus = defaultCorpus;

		for (String oneExtraRequestDataName : request.getExtraRequestDataNames()) {
			if ("x-fcs-context".equals(oneExtraRequestDataName)) {
				fcsContextCorpus = request.getExtraRequestData("x-fcs-context");
				break;
			}
		}
		return fcsContextCorpus;
	}

	/**
	 * Convenience method for parsing a string to boolean. Values <code>1</code>,
	 * <code>true</code>, <code>yes</code> yield a <em>true</em> boolean value as a
	 * result, all others (including <code>null</code>) a <em>false</em> boolean
	 * value.
	 *
	 * @param value
	 *            the string to parse
	 * @return <code>true</code> if the supplied string was considered something
	 *         representing a <em>true</em> boolean value, <code>false</code>
	 *         otherwise
	 */
	protected static boolean parseBoolean(String value) {
		if (value != null) {
			return value.equals("1") || Boolean.parseBoolean(value);
		}
		return false;
	}

	/**
	 * New method (no part of SimpleEndpointSearchEngineBase)
	 * 
	 * 1-parameter version of translateQuery, which calls the main translateQuery
	 * method, which requires 2 parameters
	 * 
	 * @param request SRU request
	 * @return translated query
	 * @throws SRUException (an SRU exception)
	 */
	public static String translateQuery(SRURequest request) throws SRUException {
		return translateQuery(request, null);
	}

	/**
	 * New method (no part of SimpleEndpointSearchEngineBase)
	 * 
	 * Translate a CQL or a FCS-QL query into a CQP query
	 * 
	 * References:
	 * 
	 * CQL : https://en.wikipedia.org/wiki/Contextual_Query_Language ---
	 * 
	 * CQP : http://cwb.sourceforge.net/files/CQP_Tutorial/ --- (CQP is sometimes
	 * also called CQL)
	 * 
	 * FCS-QL: kind of CQP, with some specs ------ defined in
	 * https://office.clarin.eu/v/CE-2017-1046-FCS-Specification.pdf
	 * 
	 * 
	 * 
	 * CQL can do simple queries like: dinosaur --- "complete dinosaur"
	 * 
	 * it can contain boolean logic like: dinosaur or bird dinosaur not reptile
	 * 
	 * and it can access publication indexes like: publicationYear &lt; 1980 date
	 * within "2002 2005"
	 * 
	 * etc.
	 * 
	 * 
	 * FCS-QL can do queries like: [word="dinosaur"] ------
	 * [word="complete"][word="dinosaur"]
	 * 
	 * it can contain boolean logic like: [word="dinosaur|bird"]
	 * 
	 * but it has no means to access publication indexes...
	 * 
	 * @param request an SRU request
	 * @param conversion a conversion engine
	 * @return translated Query
	 * @throws SRUException (an SRU exception)
	 */
	public static String translateQuery(SRURequest request, ConversionEngine conversion) throws SRUException {
		String query;

		if (request.isQueryType(Constants.FCS_QUERY_TYPE_CQL)) {
			/*
			 * Got a CQL [Contextual Query Language query] (either SRU 1.1 or higher), like
			 * in:
			 * 
			 * ... operation= searchRetrieve & version= 1.2 & query= lopen & startRecord= 1
			 * & maximumRecords= 10 & recordSchema= http://clarin.eu/fcs/resource &
			 * x-fcs-context=opensonar
			 * 
			 * Translate that into a proper CQP query ...
			 */

			final CQLQueryParser.CQLQuery q = request.getQuery(CQLQueryParser.CQLQuery.class);
			query = FCSToCQPConverter.makeCQPFromCQL(q);

			// #############
			// PAY ATTENTION:
			// #############
			// according to Jan, CQL2CQP conversion is not needed for BlackLab...

		} else if (request.isQueryType(Constants.FCS_QUERY_TYPE_FCS)) {
			/*
			 * Got a FCS query (SRU 2.0), like in:
			 * 
			 * ... query= [word="lopen"] & queryType= fcs & startRecord= 1 & maximumRecords=
			 * 10 & recordSchema= http://clarin.eu/fcs/resource & x-fcs-context= chn
			 * 
			 * Translate that into a proper CQP query
			 */

			final FCSQueryParser.FCSQuery q = request.getQuery(FCSQueryParser.FCSQuery.class);

			// get the query part out of the whole FSC request

			logger.info(String.format("FCSQuery %s: raw %s", q, q.getRawQuery()));
			query = q.getRawQuery();

			if (conversion != null) {
				logger.info(String.format("Before conversion with %s: %s", conversion, query));
				final long translationStartTime = System.currentTimeMillis();
				query = conversion.translateQuery(query);
				final long translationEndTime = System.currentTimeMillis();
				logger.info("Translation: " + (translationEndTime - translationStartTime) + " ms.");
				logger.info(String.format("After conversion with %s: %s", conversion, query));
			}
		} else {
			/*
			 * Got something else we don't support. Send error ...
			 */
			throw new SRUException(SRUConstants.SRU_UNSUPPORTED_PARAMETER, "Queries with queryType '"
					+ request.getQueryType() + "' are not supported by this CLARIN-FCS Endpoint.");
		}
		return query;
	}

	private String server;

	private ConversionEngine conversionEngine = null;

	private String engineNativeUrlTemplate;

	// Object to store the endpoint description into
	protected EndpointDescription endpointDescription;

	// Empty constructor, needed by CorpusDependentEngine subclass
	public BasicEndpointSearchEngine() {

	}

	// Constructor with arguments, called by BlacklabServerEndpointSearchEngine and
	// NederlabEndpointSearchEngine subclasses
	public BasicEndpointSearchEngine(String server, ConversionEngine conversionEngine, 
			int restrictTotalNumberOfResults, String engineNativeUrlTemplate) {
		this.server = server;
		this.conversionEngine = conversionEngine;
		this.restrictTotalNumberOfResults = restrictTotalNumberOfResults;
		this.engineNativeUrlTemplate = engineNativeUrlTemplate;
	}

	protected EndpointDescription createEndpointDescription(ServletContext context, SRUServerConfig config,
			Map<String, String> params) throws SRUConfigException {

		URL url = new FileUtils(context, "endpoint-description.xml").readConfigFileAsURL();
		return SimpleEndpointDescriptionParser.parse(url);
	}

	/**
	 * Destroy the search engine. Override this method for any cleanup the search
	 * engine needs to perform upon termination.
	 */
	protected void doDestroy() {

	}

	/**
	 * Initialize the search engine. This initialization should be tailored towards
	 * your environment and needs.
	 *
	 * @param context
	 *            the ServletContext for the Servlet
	 * @param config
	 *            the SRUServerConfig object for this search engine
	 * @param queryParserBuilder
	 *            the SRUQueryParserRegistry.Builder object to be used for
	 *            this search engine. Use to register additional query parsers with
	 *            the SRUServer.
	 * @param params
	 *            additional parameters gathered from the Servlet configuration and
	 *            Servlet context.
	 * @throws SRUConfigException
	 *             if an error occurred
	 */
	protected void doInit(ServletContext context, SRUServerConfig config,
			SRUQueryParserRegistry.Builder queryParserBuilder, Map<String, String> params) throws SRUConfigException {

		doInit(config, queryParserBuilder, params);
	}

	protected void doInit(SRUServerConfig config, SRUQueryParserRegistry.Builder queryParserBuilder,
			Map<String, String> params) throws SRUConfigException {

		logger.info("KorpEndpointSearchEngine::doInit {}", config.getPort());
		// List<String> openCorpora = ServiceInfo.getModernCorpora();
		// openCorporaInfo = CorporaInfo.getCorporaInfo(openCorpora);
	}

	/**
	 * Handle a <em>scan</em> operation. The default implementation is a no-op.
	 * Override this method, if you want to provide a custom behavior.
	 *
	 * @param config
	 *            the <code>SRUEndpointConfig</code> object that contains the
	 *            endpoint configuration
	 * @param request
	 *            the <code>SRURequest</code> object that contains the request made
	 *            to the endpoint
	 * @param diagnostics
	 *            the <code>SRUDiagnosticList</code> object for storing non-fatal
	 *            diagnostics
	 * @return a <code>SRUScanResultSet</code> object or <code>null</code> if this
	 *         operation is not supported by this search engine
	 * @throws SRUException
	 *             if an fatal error occurred
	 */
	@Override
	protected SRUScanResultSet doScan(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException {
		// final CQLNode scanClause = request.getScanClause();
		// if (scanClause instanceof CQLTermNode) {
		// final CQLTermNode root = (CQLTermNode) scanClause;
		// final String index = root.getIndex();
		// throw new SRUException(SRUConstants.SRU_UNSUPPORTED_INDEX, index,
		// "scan operation on index '" + index + "' is not supported");
		// } else {
		// throw new SRUException(SRUConstants.SRU_QUERY_FEATURE_UNSUPPORTED,
		// "Scan clause too complex.");
		// }
		return null;
	}

	public ConversionEngine getConversionEngine() {
		return conversionEngine;
	}

	public String getEngineNativeUrlTemplate() {
		return engineNativeUrlTemplate;
	}

	public String getServer() {
		return server;
	}

	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException {
		return null;
	}

	public void setConversionEngine(ConversionEngine conversionEngine) {
		this.conversionEngine = conversionEngine;
	}

	public void setEngineNativeUrlTemplate(String engineNativeUrlTemplate) {
		this.engineNativeUrlTemplate = engineNativeUrlTemplate;
	}
}
