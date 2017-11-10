package clariah.fcs;

import java.net.URI;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class Kwic 
{
	public int hitStart;
	public int hitEnd;
	
	public String defaultProperty = "word";
	
	public List<String> tokenPropertyNames = new ArrayList<>();
	public Map<String,List<String>> tokenProperties = new ConcurrentHashMap<>();
	private Map<String,String> metadata = new ConcurrentHashMap<>(); // considering the size of opensonar metadata, probably better to introduce separate document objects
	Document document = null;
	
	public Kwic translatePrefixes(Map<String,String> map)
	{
	  map.forEach(
			  (k,v) -> 
			  { 
				  tokenProperties.put(v, tokenProperties.get(k));
				  tokenProperties.remove(k);
			  }
			  );	
	  tokenPropertyNames = tokenPropertyNames.stream().map(
			  p -> map.containsKey(p)? map.get(p) : p).collect(Collectors.toList());
	  return this;
	}
	
	public  List<String> getLayer(String propertyName)
	{
		return tokenProperties.get(propertyName);
	}
	
	public String getWord(int i)
	{
	  return tokenProperties.get(defaultProperty).get(i);
	}
	
	public List<String> words()
	{
		 return tokenProperties.get(defaultProperty);
	}
	
	public int size()
	{
	    return words().size();
	}
	
	public String get(String pname, int i)
	{
		return getLayer(pname).get(i);
	}
	
	public URI layerURL(String pname)
	{
		try {
			return new URI("http://www.ivdnt.org/annotation-layers/" + pname);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString()
	{
		List<String> tokens = new ArrayList<String>();
		List<String> words = words();
		for (int i=0; i < words.size(); i++)
		{
			String p =  (hitStart <= i && i <= hitEnd)?">>":"";
			tokens.add(p + words.get(i));
		}
		String s = String.format("Kwic(%d,%d):", hitStart, hitEnd);
		return s + tokens.toString();
	}

	public Map<String,String> getMetadata() 
	{
		return metadata;
	}

	public void setMetadata(Map<String,String> metadata) {
		this.metadata = metadata;
	}
}

/**
 * Wat gaan we met metadata doen? In een CMDI profiel stoppen?
 * Hoe halen we dat op bij nederlab?
 * Voor blacklab server zelf maken uit de aanwezige metadata.
 * https://www.clarin.eu/sites/default/files/CE-2014-0317-CLARIN_FCS_Specification_DataViews_1_0.pdf: 
 * 
 * <!-- potential @pid and @ref attributes omitted -->
<fcs:DataView type="application/x-cmdi+xml">
 <cmdi:CMD xmlns:cmdi="http://www.clarin.eu/cmd/" CMDVersion="1.1">
 <!-- content omitted -->
 </cmdi:CMD>
</fcs:DataView>
<!-- potential @pid attribute omitted -->
<fcs:DataView type="application/x-cmdi+xml"
 ref="http://repos.example.org/resources/4711/0815.cmdi" />
 */

