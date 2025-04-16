package org.ivdnt.fcs.endpoint.nederlab;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.ivdnt.fcs.endpoint.nederlab.client.NederlabClient;
import org.ivdnt.fcs.endpoint.nederlab.client.QueryTemplate;
import org.ivdnt.fcs.endpoint.nederlab.results.NederlabResultSet;
import org.ivdnt.fcs.results.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NederlabQuery extends org.ivdnt.fcs.client.Query {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// template needed to build a well formed Nederlab query

	private QueryTemplate nederlabQueryTemplate;
	private QueryTemplate nederlabDocumentQueryTemplate;
	private List<String> nederlabExtraResponseFields;
	private ServletContext servletContext;

	ConcurrentHashMap<String, String> prefixMapping = new ConcurrentHashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7521524109418078231L;

		{
			put("t", "word");
		}
	};

	// --------------------------------------------------------------------

	/**
	 * NederlabQuery constructor
	 * 
	 * @param server
	 *            a URL string
	 * 
	 * @param corpus
	 *            a corpus name, like 'opensonar' (those are declared as Resource
	 *            pid's in WEB-INF/endpoint-description.xml)
	 * 
	 * @param cqpQuery
	 *            a query like [word='lopen']
	 * @param servletContext context Cache

         * @param startPosition  start Position
         * @param maximumResults maximum nr Results
         * @param restrictTotalNumberOfResults restrict Total Number Of Results
	 * @param nederlabQueryTemplate nederlab Query Template
	 * @param nederlabDocumentQueryTemplate nederlab Document Query Template
	 * @param  engineNativeUrlTemplate engine Native Url Template
	 * @param  nederlabExtraResponseFields nederlab ExtraResponseFields
	 */
	public NederlabQuery(ServletContext servletContext, String server, String corpus, String cqpQuery, int startPosition,
			int maximumResults, int restrictTotalNumberOfResults, QueryTemplate nederlabQueryTemplate, QueryTemplate nederlabDocumentQueryTemplate,
			String engineNativeUrlTemplate, List<String> nederlabExtraResponseFields) {
		super(server, corpus, cqpQuery, startPosition, maximumResults, restrictTotalNumberOfResults, engineNativeUrlTemplate);

		this.servletContext = servletContext;
		// template to build Nederlab queries
		this.nederlabQueryTemplate = nederlabQueryTemplate;
		this.nederlabDocumentQueryTemplate = nederlabDocumentQueryTemplate;

		this.nederlabExtraResponseFields = nederlabExtraResponseFields;

		// make sure the CQL query
		// has the right quotes and parameter names

		String cqlQuery = this.getCqpQuery();
		cqlQuery = cqlQuery.replaceAll("word *=", "t_lc="); // hm ugly hacks
		cqlQuery = cqlQuery.replaceAll("'", "\"");
		this.setCqpQuery(cqlQuery);

		// System.err.println("CQP to nederlab:" + this.getCqpQuery());

		// Form native URL based on template and URL-encoded query string
		String engineNativeUrl = "";
		if (!engineNativeUrlTemplate.isEmpty()) {
			engineNativeUrl = engineNativeUrlTemplate;
			try {
				engineNativeUrl += URLEncoder.encode(this.getCqpQuery(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Not able to encode query URL " + this.getCqpQuery(), e);
			}
		}
		this.setEngineNativeUrl(engineNativeUrl);
	}

	// --------------------------------------------------------------------

	/**
	 * Execute a prepared search (prepared in NederlabEndpointSearchEngine.search)
	 * and put the results into a FCS ResultSet
	 */
	public org.ivdnt.fcs.results.ResultSet execute() {

		// search

		NederlabClient nederlabClient = new NederlabClient(this.servletContext, this.nederlabQueryTemplate,
				this.nederlabDocumentQueryTemplate, this.getServer(), this.nederlabExtraResponseFields);

		NederlabResultSet nederlabResultSet = nederlabClient.doSearch(this.getCqpQuery(), this.getStartPosition(),
				this.getMaximumResults());
		
		// We first set total number of results
		this.setTotalNumberOfResults(nederlabResultSet.getTotalNumberOfHits());

		// get results

		List<org.ivdnt.fcs.results.Kwic> hits = nederlabResultSet.getResults().stream()
				.map(h -> h.toKwic().translatePrefixes(this.prefixMapping)) // another ugly hack
				.collect(Collectors.toList());
		

		// build FCS ResultSet

		ResultSet fcsResultSet = new ResultSet();
		fcsResultSet.setHits(hits);
		fcsResultSet.setQuery(this);
		
		// We not get total number of results, where it can be restricted by a threshold.
		fcsResultSet.setTotalNumberOfResults(this.getTotalNumberOfResults());

		logger.info("Result set determined " + fcsResultSet.toString());
		return fcsResultSet;
	}

}
