package clariah.fcs.results;

import java.net.URI;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * This class is used to store FCS keywords in context (kwic)
 * as part of the ResultSet 
 * (which is part of a FcsSearchResultSet = main result object) 
 * 
 * @author jesse
 *
 */
public class Kwic 
{
	
	// start and end position of a keyword, within its context
	// (t.i. the token that matches the query)
	
	private int hitStart;
	private int hitEnd;
	
	// 
	
	private String defaultProperty = "word";
	
	private List<String> 				tokenPropertyNames = new ArrayList<>();
	private Map<String, List<String>>	tokenProperties = new ConcurrentHashMap<>();
	private Map<String,String> 			metadata = new ConcurrentHashMap<>(); // considering the size of opensonar metadata, probably better to introduce separate document objects
	Document 							document = null;
	
	// -----------------------------------------------------------------------------------
	
	public Kwic translatePrefixes(Map<String, String> map)
	{
	  map.forEach(
			  (k,v) -> 
			  { 
				  this.tokenProperties.put(v, this.tokenProperties.get(k));
				  this.tokenProperties.remove(k);
			  }
			  );	
	  
	  this.tokenPropertyNames = this.tokenPropertyNames.stream().map(
			  p -> map.containsKey(p)? map.get(p) : p).collect(Collectors.toList());
	  return this;
	}
	
	// -----------------------------------------------------------------------------------
	// getters 
	
	
	public String getDefaultProperty() {
		return this.defaultProperty;
	}
	
	public List<String> getLayer(String propertyName)
	{
		return this.tokenProperties.get(propertyName);
	}
	
	public String getWord(int i)
	{
	  return this.tokenProperties.get(defaultProperty).get(i);
	}
	
	public List<String> words()
	{
		 return this.tokenProperties.get(defaultProperty);
	}
	
	public int size()
	{
	    return words().size();
	}
	
	public String get(String pname, int i)
	{
		return getLayer(pname).get(i);
	}
	
	public URI getLayerURL(String pname)
	{
		try {
			return new URI("http://www.ivdnt.org/annotation-layers/" + pname);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Map<String,String> getMetadata() 
	{
		return this.metadata;
	}	
	
	public int getHitStart()
	{
	  return this.hitStart;
	}
	
	public int getHitEnd()
	{
	  return this.hitEnd;
	}
	
	public List<String> getTokenPropertyNames() {
		return this.tokenPropertyNames;
	}
	
	
	// -----------------------------------------------------------------------------------
	// setters
	
	public void setMetadata(Map<String,String> metadata) {
		this.metadata = metadata;
	}
	
	public void setHitStart(int hitStart) {
		this.hitStart = hitStart;
	}
	
	
	public void setHitEnd(int hitEnd) {
		this.hitEnd = hitEnd;
	}
	
	
	public void addTokenPropertyName(String pname) {
		this.tokenPropertyNames.add(pname);
	}
	
	public void addTokenPropertyNames(Set<String> pnames) {
		this.tokenPropertyNames.addAll(pnames);
	}
	
	
	public void addTokenProperties(String pname, List<String> properties) {
		this.tokenProperties.put(pname, properties);
	}
	
	
	// -----------------------------------------------------------------------------------
	
	public String toString()
	{
		List<String> tokens = new ArrayList<String>();
		List<String> words = words();
		for (int i=0; i < words.size(); i++)
		{
			String p =  (this.hitStart <= i && i <= this.hitEnd) ? ">>" : "";
			tokens.add(p + words.get(i));
		}
		String s = String.format("Kwic(%d,%d):", this.hitStart, this.hitEnd);
		return s + tokens.toString();
	}

	
	// -----------------------------------------------------------------------------------
	
}

/**
 * Wat gaan we met metadata doen? In een CMDI profiel stoppen?
 * Hoe halen we dat op bij nederlab?
 * Voor blacklab server zelf maken uit de aanwezige metadata.
 * https://www.clarin.eu/sites/default/files/CE-2014-0317-CLARIN_FCS_Specification_DataViews_1_0.pdf: 
 * 
 * <!-- potential @pid and @ref attributes omitted -->
 * <fcs:DataView type="application/x-cmdi+xml">
 *  <cmdi:CMD xmlns:cmdi="http://www.clarin.eu/cmd/" CMDVersion="1.1">
 *   <!-- content omitted -->
 *    </cmdi:CMD>
 *    </fcs:DataView>
 *    <!-- potential @pid attribute omitted -->
 *    <fcs:DataView type="application/x-cmdi+xml" ref="http://repos.example.org/resources/4711/0815.cmdi" />
 */

