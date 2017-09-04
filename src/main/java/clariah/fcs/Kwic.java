package clariah.fcs;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.*;

public class Kwic 
{
	public int hitStart;
	public int hitEnd;
	
	public String defaultProperty = "word";
	
	public List<String> tokenPropertyNames = new ArrayList<>();
	public Map<String,List<String>> tokenProperties = new HashMap<>();
	
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
}
