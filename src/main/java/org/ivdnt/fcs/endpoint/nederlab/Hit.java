package org.ivdnt.fcs.endpoint.nederlab;
import java.util.*;
import java.util.stream.Collectors;

import org.ivdnt.util.StringUtils;

public class Hit 
{
	public int startPosition;
	public int endPosition;
	public List<String> properties = new ArrayList<String>();
	public List<Token> tokens = new ArrayList<>();
	public String documentKey = null;
	public Document document ;
	public Set<String> knownPrefixes = new HashSet<String>();
	public int getHitStart()
	{
		return startPosition;
		
	}
	
	public int getHitEnd()
	{
		return endPosition;
	}
	
	public Hit(List<TokenProperty> unordered)
	{
		Map<Integer,Token> m = new HashMap<>();
		for (TokenProperty tp: unordered)
		{
			Token t = m.get(tp.positionStart);
			if (t == null)
			{
				t = new Token();
				t.startPosition = tp.positionStart;
				m.put(tp.positionStart, t);
			}
			if (tp.prefix.equals("t"))
				t.contentToken = true;
			knownPrefixes.add(tp.prefix);
			t.tokenProperties.add(tp);
		}
		this.tokens.addAll(m.values());
		Collections.sort(tokens, (t1,t2) -> Integer.compare(t1.startPosition, t2.startPosition));
		//this.properties = knownProperties;
	}
	
	//public 
	public String toString()
	{
		List<String> lines = tokens.stream().map(t -> t.toString()).collect(Collectors.toList());
		return this.document.NLTitle_title + "\n"  
				+ StringUtils.join(lines, "\n");
	}
}
