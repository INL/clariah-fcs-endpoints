package org.ivdnt.fcs.endpoint.nederlab.client;
import java.util.Map;

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
	
	
	public QueryTemplate(String nederlabQueryTemplate) {
		
		this.template = nederlabQueryTemplate;
	}
	
	
	// ------------------------------------------------------------------
	// expand the base template with extra parameters
	// or fill in some values which are not pre-filled in the template
	
	public String expandTemplate(Map<String, String> e)
	{
		String expandedTemplate = this.template;
		
		for (String k: e.keySet())
			expandedTemplate = expandedTemplate.replaceAll(k, e.get(k));
		
		return expandedTemplate;
	}
	
	
	// ------------------------------------------------------------------
	//
	// default hard coded template
	//
	// DON'T REMOVE THIS: this template can be used as an example
	//                    to build other valid Nederlab templates...
	//
	// useful reference: 
	// http://www.nederlab.nl/onderzoeksportaal/sites/nederlab/javascript/nederlab/controller/querybuilder.js?version=2017-10-12
	
	/*
	static String oldTpl =
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
			//"          \"prefix\": \"t,pos,lemma\","+
			"          \"prefix\": \"t,lemma, pos, entity, feat.tokentype, feat.pos, feat.ntype, feat.getal, feat.graad, feat.genus, feat.naamval, feat.positie, feat.buiging, feat.getal-n, feat.wvorm, feat.pvtijd, feat.pvagr, feat.numtype,feat.vwtype, feat.pdtype, feat.persoon, feat.status, feat.npagr, feat.lwtype, feat.vztype, feat.conjtype, feat.spectype\","+ 
			"          \"left\": _CONTEXT_,"+
			"          \"right\": _CONTEXT_ "+
			"        }"+
			"      ]"+
			"    }"+
			"  },"+
			"  \"cache\": true"+
			"}";
	*/
	
}
