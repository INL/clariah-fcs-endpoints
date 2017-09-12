/**
 *
 * @license http://www.gnu.org/licenses/gpl-3.0.txt
 *  GNU General Public License v3
 */
package org.ivdnt.fcs.endpoint.common;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLNotNode;
import org.z3950.zing.cql.CQLOrNode;
import org.z3950.zing.cql.CQLTermNode;

import eu.clarin.sru.server.CQLQueryParser;
import eu.clarin.sru.server.CQLQueryParser.CQLQuery;
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
import eu.clarin.sru.server.fcs.DataView;
import eu.clarin.sru.server.fcs.EndpointDescription;
import eu.clarin.sru.server.fcs.FCSQueryParser;
import eu.clarin.sru.server.fcs.FCSQueryParser.FCSQuery;
import eu.clarin.sru.server.fcs.Layer;
import eu.clarin.sru.server.fcs.ResourceInfo;
import eu.clarin.sru.server.fcs.SimpleEndpointSearchEngineBase;
import eu.clarin.sru.server.fcs.parser.Expression;
import eu.clarin.sru.server.fcs.parser.Operator;
import eu.clarin.sru.server.fcs.parser.QueryNode;
import eu.clarin.sru.server.fcs.parser.QuerySegment;
import eu.clarin.sru.server.fcs.utils.SimpleEndpointDescriptionParser;

import org.w3c.dom.Document;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import clariah.fcs.mapping.Conversion;



/**
 * Base class for  endpoint search engines, mainly copies from the Korp reference example
 *
 */
public class BasicEndpointSearchEngine extends SimpleEndpointSearchEngineBase {
	private static final String X_FCS_ENDPOINT_DESCRIPTION = "x-fcs-endpoint-description";
	private static final String ED_NS = "http://clarin.eu/fcs/endpoint-description";
	private static final String ED_PREFIX = "ed";
	private static final int ED_VERSION = 2;

	private static final Logger LOG = LoggerFactory.getLogger(BasicEndpointSearchEngine.class);
	protected EndpointDescription endpointDescription;
	public static final String RESOURCE_INVENTORY_URL = "se.gu.spraakbanken.fcs.korp.sru.resourceInventoryURL";

	protected EndpointDescription createEndpointDescription(ServletContext context, SRUServerConfig config,
			Map<String, String> params) throws SRUConfigException {
		try {
			URL url = null;
			String riu = params.get(RESOURCE_INVENTORY_URL);
			if ((riu == null) || riu.isEmpty()) {
				url = context.getResource("/WEB-INF/endpoint-description.xml");
				LOG.debug("using bundled 'endpoint-description.xml' file");
			} else {
				url = new File(riu).toURI().toURL();
				LOG.debug("using external file '{}'", riu);
			}

			return SimpleEndpointDescriptionParser.parse(url);
		} catch (MalformedURLException mue) {
			throw new SRUConfigException("Malformed URL for initializing resource info inventory", mue);
		}
	}

	/**
	 * Initialize the search engine. This initialization should be tailored towards
	 * your environment and needs.
	 *
	 * @param context
	 *            the {@link ServletContext} for the Servlet
	 * @param config
	 *            the {@link SRUServerConfig} object for this search engine
	 * @param queryParserBuilder
	 *            the {@link SRUQueryParserRegistry.Builder} object to be used for
	 *            this search engine. Use to register additional query parsers with
	 *            the {@link SRUServer}.
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
		LOG.info("KorpEndpointSearchEngine::doInit {}", config.getPort());
		//List<String> openCorpora = ServiceInfo.getModernCorpora();
		//openCorporaInfo = CorporaInfo.getCorporaInfo(openCorpora);
	}

	/**
	 * Destroy the search engine. Override this method for any cleanup the search
	 * engine needs to perform upon termination.
	 */
	protected void doDestroy() {

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

	private void writeEndpointDescription(XMLStreamWriter writer) throws XMLStreamException {
		writer.setPrefix(ED_PREFIX, ED_NS);
		writer.writeStartElement(ED_NS, "EndpointDescription");
		writer.writeNamespace(ED_PREFIX, ED_NS);
		writer.writeAttribute("version", Integer.toString(ED_VERSION));

		// Capabilities
		writer.writeStartElement(ED_NS, "Capabilities");
		for (URI capability : endpointDescription.getCapabilities()) {
			writer.writeStartElement(ED_NS, "Capability");
			writer.writeCharacters(capability.toString());
			writer.writeEndElement(); // "Capability" element
		}
		writer.writeEndElement(); // "Capabilities" element

		// SupportedDataViews
		writer.writeStartElement(ED_NS, "SupportedDataViews");
		for (DataView dataView : endpointDescription.getSupportedDataViews()) {
			writer.writeStartElement(ED_NS, "SupportedDataView");
			writer.writeAttribute("id", dataView.getIdentifier());
			String s;
			switch (dataView.getDeliveryPolicy()) {
			case SEND_BY_DEFAULT:
				s = "send-by-default";
				break;
			case NEED_TO_REQUEST:
				s = "need-to-request";
				break;
			default:
				throw new XMLStreamException(
						"invalid value for payload delivery policy: " + dataView.getDeliveryPolicy());
			} // switch
			writer.writeAttribute("delivery-policy", s);
			writer.writeCharacters(dataView.getMimeType());
			writer.writeEndElement(); // "SupportedDataView" element
		}
		writer.writeEndElement(); // "SupportedDataViews" element

		// SupportedLayers
		final List<Layer> layers = endpointDescription.getSupportedLayers();
		if (layers != null) {
			writer.writeStartElement(ED_NS, "SupportedLayers");
			for (Layer layer : layers) {
				writer.writeStartElement(ED_NS, "SupportedLayer");
				writer.writeAttribute("id", layer.getId());
				writer.writeAttribute("result-id", layer.getResultId().toString());
				if (layer.getContentEncoding() == Layer.ContentEncoding.EMPTY) {
					writer.writeAttribute("type", "empty");
				}
				if (layer.getQualifier() != null) {
					writer.writeAttribute("qualifier", layer.getQualifier());
				}
				if (layer.getAltValueInfo() != null) {
					writer.writeAttribute("alt-value-info", layer.getAltValueInfo());
					if (layer.getAltValueInfoURI() != null) {
						writer.writeAttribute("alt-value-info-uri", layer.getAltValueInfoURI().toString());
					}
				}
				writer.writeCharacters(layer.getType());
				writer.writeEndElement(); // "SupportedLayer" element
			}
			writer.writeEndElement(); // "SupportedLayers" element

		}

		// Resources
		try {
			List<ResourceInfo> resources = endpointDescription.getResourceList(EndpointDescription.PID_ROOT);
			writeResourceInfos(writer, resources);
		} catch (SRUException e) {
			throw new XMLStreamException("error retriving top-level resources", e);
		}
		writer.writeEndElement(); // "EndpointDescription" element
	}

	private void writeResourceInfos(XMLStreamWriter writer, List<ResourceInfo> resources) throws XMLStreamException {
		if (resources == null) {
			throw new NullPointerException("resources == null");
		}
		if (!resources.isEmpty()) {
			writer.writeStartElement(ED_NS, "Resources");

			for (ResourceInfo resource : resources) {
				writer.writeStartElement(ED_NS, "Resource");
				writer.writeAttribute("pid", resource.getPid());

				// title
				final Map<String, String> title = resource.getTitle();
				for (Map.Entry<String, String> i : title.entrySet()) {
					writer.setPrefix(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
					writer.writeStartElement(ED_NS, "Title");
					writer.writeAttribute(XMLConstants.XML_NS_URI, "lang", i.getKey());
					writer.writeCharacters(i.getValue());
					writer.writeEndElement(); // "title" element
				}

				// description
				final Map<String, String> description = resource.getDescription();
				if (description != null) {
					for (Map.Entry<String, String> i : description.entrySet()) {
						writer.writeStartElement(ED_NS, "Description");
						writer.writeAttribute(XMLConstants.XML_NS_URI, "lang", i.getKey());
						writer.writeCharacters(i.getValue());
						writer.writeEndElement(); // "Description" element
					}
				}

				// landing page
				final String landingPageURI = resource.getLandingPageURI();
				if (landingPageURI != null) {
					writer.writeStartElement(ED_NS, "LandingPageURI");
					writer.writeCharacters(landingPageURI);
					writer.writeEndElement(); // "LandingPageURI" element
				}

				// languages
				final List<String> languages = resource.getLanguages();
				writer.writeStartElement(ED_NS, "Languages");
				for (String i : languages) {
					writer.writeStartElement(ED_NS, "Language");
					writer.writeCharacters(i);
					writer.writeEndElement(); // "Language" element

				}
				writer.writeEndElement(); // "Languages" element

				// available data views
				StringBuilder sb = new StringBuilder();
				for (DataView dataview : resource.getAvailableDataViews()) {
					if (sb.length() > 0) {
						sb.append(" ");
					}
					sb.append(dataview.getIdentifier());
				}
				writer.writeEmptyElement(ED_NS, "AvailableDataViews");
				writer.writeAttribute("ref", sb.toString());

				final List<Layer> layers = resource.getAvailableLayers();
				if (layers != null) {
					sb = new StringBuilder();
					for (Layer layer : resource.getAvailableLayers()) {
						if (sb.length() > 0) {
							sb.append(" ");
						}
						sb.append(layer.getId());
					}
					writer.writeEmptyElement(ED_NS, "AvailableLayers");
					writer.writeAttribute("ref", sb.toString());
				}

				// child resources
				List<ResourceInfo> subs = resource.getSubResources();
				if ((subs != null) && !subs.isEmpty()) {
					writeResourceInfos(writer, subs);
				}

				writer.writeEndElement(); // "Resource" element
			}
			writer.writeEndElement(); // "Resources" element
		}
	}

	public SRUSearchResultSet search(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics)
			throws SRUException {
		return null;
	}

	public static String getCorpusNameFromRequest(SRURequest request, String defaultCorpus) 
	{
		boolean hasFcsContextCorpus = false;
		
		String fcsContextCorpus = defaultCorpus;
		
		for (String erd : request.getExtraRequestDataNames()) {
			if ("x-fcs-context".equals(erd)) {
				hasFcsContextCorpus = true;
				fcsContextCorpus = request.getExtraRequestData("x-fcs-context"); // TODO fix this in corpusinfo implementation
				break;
			}
		}
		return fcsContextCorpus;
	}

	public static String translateQuery(SRURequest request) throws SRUException 
	{
		return translateQuery(request,null);
	}

	public static String translateQuery(SRURequest request, Conversion conversion) throws SRUException {
		String query;
	
		if (request.isQueryType(Constants.FCS_QUERY_TYPE_CQL)) {
			/*
			 * Got a CQL query (either SRU 1.1 or higher). Translate to a proper CQP query
			 * ...
			 */
			final CQLQueryParser.CQLQuery q = request.getQuery(CQLQueryParser.CQLQuery.class);
			query = FCSToCQPConverter.makeCQPFromCQL(q);
		} else if (request.isQueryType(Constants.FCS_QUERY_TYPE_FCS)) {
			/*
			 * Got a FCS query (SRU 2.0). Translate to a proper CQP query
			 */
			
			final FCSQueryParser.FCSQuery q = request.getQuery(FCSQueryParser.FCSQuery.class);
			System.err.println(String.format("FCSQuery %s: raw %s", q, q.getRawQuery()));
			query = q.getRawQuery();
			
			if (conversion != null)
			{
				System.err.println(String.format("Before conversion with %s: %s",  conversion, query));
			    query = conversion.translateQuery(query);
			    System.err.println(String.format("After conversion with %s: %s",  conversion, query));
			}
			        // do not parse the query. TODO real mapping component!
					// FCSToCQPConverter.makeCQPFromFCS(q);
		} else {
			/*
			 * Got something else we don't support. Send error ...
			 */
			throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN, "Queries with queryType '"
					+ request.getQueryType() + "' are not supported by this CLARIN-FCS Endpoint.");
		}
		return query;
	}
}
