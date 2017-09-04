package org.ivdnt.fcs.endpoint.nederlab.stuff;
import java.util.*;

public class Token 
{
	public int startPosition;
	public int endPosition;
	public List<TokenProperty> tokenProperties = new ArrayList<>();
	boolean contentToken = false;
	//Set<String> properties = new HashSet<String>();
	
	
	public String getProperty(String propertyName)
	{
		for (TokenProperty tp: tokenProperties)
		{
			if (tp.prefix.equals(propertyName))
				return tp.value;
		}
		return null;
	}
	
	public String toString()
	{
		//List<String> l = new ArrayList();
		tokenProperties.sort( (p1,p2) -> p1.prefix.compareTo(p2.prefix));
		String r = this.startPosition + ":";
		for (TokenProperty tp: tokenProperties)
		{
			r += tp.prefix + "=" + tp.value + " ";
		}
		return r;
		
		//for (String)
	}
}
