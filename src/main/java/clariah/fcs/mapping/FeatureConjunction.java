package clariah.fcs.mapping;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ivdnt.util.StringUtils;

/**
 * This class is an extension of the ConcurrentHashMap class
 * with some extra get / toString-like methods
 * 
 * @author jesse
 *
 */
public class FeatureConjunction extends ConcurrentHashMap<String, Set<String>> 
{
	

	// ------------------------------------------------
	// setter
	
	public void put(String name, String value)
	{
		Set<String> v = this.get(name);
		if (v == null || v.isEmpty()) 
			super.put(name, v = new HashSet<String>());
		v.add(value);
	}
	
	
	// ------------------------------------------------
	// getters

	@Override
	public Set<String> get(Object s) // oho this is silly..
	{
		Set<String> r = super.get(s);
		if (r != null)
			return r;
		else // return the empty set
			return new HashSet<String>();
	}
	
	public String getValues(String name)
	{
		return StringUtils.join(get(name), MappingConstants.multiValueJoiner+"");
	}
	
	public Stream<Feature> getFeatures() 
	{
		return this.keySet().stream().map(k -> new Feature(k, get(k)));
	}

	public boolean hasFeature(String fname)
	{
		return this.containsKey(fname) || !this.get(fname).isEmpty();
	}

	public boolean hasFeature(String name, String val) 
	{

		return this.hasFeature(name) && this.get(name).contains(val);
	}

	
	// ------------------------------------------------
	// kind of toString() methods
	
	public String asCQL()
	{
		Set<String> clauses = 
				this.keySet().stream().map(
						k -> new Feature(k, get(k)).asCQL()
				).collect(Collectors.toSet());
		
		return "[" + StringUtils.join(clauses, " & ") + "]";
	}
	
	public String asRegexInTag()
	{
		Set<String> clauses = 
				this.keySet().stream().map(
						k -> new Feature(k, get(k)).asRegexInTag("pos")
				).collect(Collectors.toSet());
		
		return "[" + StringUtils.join(clauses, " & ") + "]";
	}
	
	
	
	// ---------------------------------
	// NOT IN USE
	
	public Set<String> keySetX()
	{
		Set<String> Z = new HashSet<String>();
		for (String s: super.keySet())
		{
			Z.add(s);
		}
		return Z;
	}
}