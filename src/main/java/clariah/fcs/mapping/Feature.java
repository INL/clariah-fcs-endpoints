package clariah.fcs.mapping;
import java.util.*;

import org.ivdnt.util.StringUtils;

public class Feature 
{
	private String name;
	private Set<String> values = new HashSet<String>();
	
	
	// -----------------------------------------------------------------------
	// constructors
	
	public Feature(String name, Set<String> values)
	{
		this.name = name;
		this.values = values;
	}

	public Feature(String name, String values)
	{
		this.name = name;
		Set<String> v = new HashSet<>();
		for (String s: values.split(MappingConstants.multiValueSeparator)) v.add(s);
		//System.err.println(v);
		this.values = v;
	}
	
	// -----------------------------------------------------------------------
	// getters
	
	public String getFeatureName(){
		return this.name;
	}
	
	public Set<String> getValues(){
		return this.values;
	}
	
	
	// -----------------------------------------------------------------------
	
	
	public String asCQL()
	{
		String vals = StringUtils.join(values, "|");
		return String.format("%s='%s'", name, vals);
	}

	public String asRegexInTag(String CQLTagField)
	{
		String vals = StringUtils.join(values, "|");
		if (name.equals(CQLTagField))
			return String.format("%s='^(%s).*'",name,vals);
		else
			return String.format("%s='.*%s\\s*=(%s).*'", CQLTagField, name, vals);
	}
	
	public boolean equals(Object other)
	{
		try
		{
			Feature f = (Feature) other;
			return f.name.equals(this.name) && f.values.equals(this.values);
		} catch (Exception e)
		{
			return false;
		}
	}
	public int hashCode()
	{
		return name.hashCode() + values.hashCode();
	}
}
