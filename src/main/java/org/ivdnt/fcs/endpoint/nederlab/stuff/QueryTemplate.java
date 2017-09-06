package org.ivdnt.fcs.endpoint.nederlab.stuff;
import java.io.*;
import java.util.*;

public class QueryTemplate 
{
	public String template;
	public String defaultFile = "Query/template.json";
	
	private String getFile(String fileName) {

		StringBuilder result = new StringBuilder("");

		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	 }
	
	public QueryTemplate(String fileName)
	{
		readFromFile(fileName);
	}
	
	public QueryTemplate()
	{
		template = tpl;
	}
	
	public String expandTemplate(Map<String,String> e)
	{
		String t = template;
		for (String k: e.keySet())
			t = t.replaceAll(k, e.get(k));
		return t;
	}
	
	public void readFromFile(String fileName)
	{
		template = getFile(fileName);
		/*
		try
		{
		  FileReader r  = new FileReader(getFile(fileName));
		  BufferedReader b = new BufferedReader(r);
		  String l;
		  template = "";
		  while ((l = b.readLine()) != null)
		  {
			  template += l + "\n";
		  }
		  b.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		*/
	}
	// s removed from output. offsets??
	
	static String tpl ="{"+
			"  \"condition\": {"+
			"    \"type\": \"cql\","+
			"    \"field\": \"NLContent_mtas\","+
			"    \"value\": \"_QUERY_\""+
			"  },"+
			"  \"response\": {"+
			"    \"stats\": true,"+
			"    \"documents\": {"+
			"      \"number\": _NUMBER_,"+
			"      \"start\": _START_,"+
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
			"          \"key\": \"just_a_cow\","+
			"          \"output\": \"token\","+
			"          \"number\": 50, "+
			"          \"start\": 0,"+
			"          \"prefix\": \"t,pos,lemma\","+
			"          \"left\": _CONTEXT_,"+
			"          \"right\": _CONTEXT_ "+
			"        }"+
			"      ]"+
			"    }"+
			"  }"+
			"}";

}
