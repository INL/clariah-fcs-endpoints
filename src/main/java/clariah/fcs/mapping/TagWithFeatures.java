package clariah.fcs.mapping;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.ivdnt.util.StringUtils;

/**
 * 
 * @author does
 *
 *This class parses parole-style tags, type NOU(number=sg), etc
 */

public class TagWithFeatures extends HashMap<String, Set<String>> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String multiValueSeparator="|"; 
	
	
	public void put(String name, String value)
	{
		Set<String> v = this.get(name);
		if (v == null || v.isEmpty()) 
			super.put(name, v = new HashSet<String>());
		v.add(value);
		
	}
	
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
		return StringUtils.join(get(name), multiValueSeparator+"");
	}
	
	//@Override
	public Set<String> keySetX()
	{
		Set<String> Z = new HashSet<String>();
		for (String s: super.keySet())
		{
			Z.add(s);
		}
	    return Z;
		//return super.keySet();
	}
	
	public String toString()
	{
		String pos = this.getValues("pos");
		
		List<String> l = new ArrayList<String>();
		for (String name: super.keySet())
		{
			if (!name.equals("pos"))
			{
				l.add(name + "=" + getValues(name));
			}
		}
		return pos + "(" + StringUtils.join(l, ",") + ")";
	}
	
	public boolean hasFeature(String fname)
	{
		return this.containsKey(fname) || !this.get(fname).isEmpty();
	}
	
	public static TagWithFeatures parseParoleStyleTag(String tag)
	{
		TagWithFeatures t = new TagWithFeatures();
		String[] a = tag.split("\\(");
		t.put("pos", a[0]);
		if (a.length > 1)
		{
			String rest = a[1].replaceAll("\\)", "");
			String[] featuresvalues = rest.split(",");
			for (String fplusv: featuresvalues)
			{
				String[] fv = fplusv.split("=");
				if (fv.length > 1)
				{
					String name= fv[0];
					String values = fv[1];
					for (String value: values.split("\\" + multiValueSeparator+""))
					{
						t.put(name, value);
					}
				}
			}
		}
		
		return t;
	}

	public boolean hasFeature(String name, String val) 
	{
		
		return this.hasFeature(name) && this.get(name).contains(val);
	}
}