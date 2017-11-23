package org.ivdnt.fcs.endpoint.nederlab.client;
import java.util.*;

import org.ivdnt.util.FileUtils;

/**
 * This class constructs a query out of a template 
 * for Nederlab querying 
 * 
 * @author jesse / mathieu
 *
 */
public class QueryTemplate 
{
	public String template;
	public String defaultFile = "Query/template.json";
	
	
	// ------------------------------------------------------------------
	// constructors
	//
	// get the default template (hard coded) or read it from a file
	
	public QueryTemplate(String fileName)
	{
		this.template = new FileUtils().getResourceAsString(fileName);
		// s removed from output. offsets??
	}
	
	public QueryTemplate()
	{
		this.template = this.tpl; // default hard coded template
	}
	
	
	// ------------------------------------------------------------------
	// extend the base template with extra parameters
	// or fill in some values which are not pre-filled in the template
	
	public String expandTemplate(Map<String,String> e)
	{
		String t = template;
		for (String k: e.keySet())
			t = t.replaceAll(k, e.get(k));
		return t;
	}
	
	
	// ------------------------------------------------------------------
	//
	// default hard coded template
	//
	// useful reference: 
	// http://www.nederlab.nl/onderzoeksportaal/sites/nederlab/javascript/nederlab/controller/querybuilder.js?version=2017-10-12
	
	static String tpl =
			"{"+
			"  \"filter\": {"+
			"    \"list\": ["+
			"      {"+
			"        \"condition\": {"+
			"          \"type\": \"cql\","+
			"          \"field\": \"NLContent_mtas\","+
			"          \"value\": \"_QUERY_\""+
			"        }"+
			"      }"+
			"    ]"+
			"  },"+
			"  \"response\": {"+
			"    \"stats\": true,"+
			"    \"documents\": {"+
			"      \"number\": _NUMBER_,"+	// = items per page (see ./nederlab/controller/querybuilder.js)
			"      \"start\": _START_,"+
			"      \"translate\": true,"+
			"      \"fields\": ["+
			"        \"NLCore_NLIdentification_nederlabID\","+
			"        \"NLProfile_name\","+
			"        \"NLTitle_title\","+
			"        \"NLTitle_yearOfPublicationMin\","+
			"        \"NLTitle_yearOfPublicationMax\","+
			"        \"NLCore_NLAdministrative_sourceCollection\""+
			"      ]"+
			"    },"+
			"    \"mtas\": {"+
			"      \"kwic\": ["+
			"        {"+
			"          \"field\": \"NLContent_mtas\","+
			"          \"query\": {"+
			"            \"type\": \"cql\","+
			"            \"value\": \"_QUERY_\""+
			"          },"+
			"          \"key\": \"tekst\","+   
			//"          \"key\": \"_QUERY_\"0,"+    // beware: the 0 belongs here!
			"          \"output\": \"token\","+
			//"          \"number\": 50, "+			
			"          \"number\": _NUMBER_, "+			// max inline snippets
			"          \"start\": 0,"+
			"          \"prefix\": \"t,pos,lemma\","+
			"          \"left\": _CONTEXT_,"+
			"          \"right\": _CONTEXT_ "+
			"        }"+
			"      ]"+
			"    }"+
			"  },"+
			// sorting gives a 400 BAD REQUEST error
//			"  \"sort\": ["+
//			"    \"field\": \"NLContent_mtas\","+
//			"    \"direction\": \"asc\""+
//			"  ],"+
			"  \"cache\": true"+
			"}";

}
