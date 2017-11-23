package clariah.fcs.mapping;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.ivdnt.util.StringUtils;

/**
 * This class parses parole-style tags, type NOU(number=sg), etc.
 *  
 * @author does
 */

public class TagWithFeatures extends FeatureConjunction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

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



	public static TagWithFeatures parseParoleStyleTag(String tag)
	{
		TagWithFeatures t = new TagWithFeatures();
		
		
		// get the pos-tag  (string part before the brackets, which contain the features)
		
		String[] a = tag.split("\\(");
		t.put("pos", a[0]);
		
		
		// get the features
		
		if (a.length > 1)
		{
			// get and split into separate features (part before the closing bracket)
			
			String rest = a[1].replaceAll("\\)", "");
			
			String[] featuresvalues = rest.split(",");
			for (String fplusv: featuresvalues)
			{
				// key = value
				
				String[] fv = fplusv.split("=");
				if (fv.length > 1)
				{
					String name= fv[0];
					String values = fv[1];
					for (String value: values.split(MappingConstants.multiValueSeparator))
					{
						t.put(name, value);
					}
				}
			}
		}

		return t;
	}
	
	
	// --------------------------------------------------------------------
	// test only

	public static void main(String[] args)
	{
		String t = "VRB(tense=past,number=sg|pl)";
		TagWithFeatures twf = TagWithFeatures.parseParoleStyleTag(t);
		System.out.println(t + " " + twf.asCQL() + " " + twf.asRegexInTag());
	}
}