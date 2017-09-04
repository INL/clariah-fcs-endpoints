package org.ivdnt.fcs.endpoint.nederlab.stuff;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		return 
				"(" + this.startPosition + "-" + this.endPosition + ") "
				+ this.document.NLTitle_title + "\n"  
				+ StringUtils.join(lines, "; ");
	}
	
	public clariah.fcs.Kwic toKwic()
	{
		clariah.fcs.Kwic kwic = new clariah.fcs.Kwic();
		kwic.tokenPropertyNames.addAll(this.knownPrefixes);
		
		kwic.hitStart = this.getHitStart();
		kwic.hitEnd = this.getHitEnd()+1; // HM nog even naar kijken, gaat niet helemaal lekker zo
		
		System.err.println("Current hit to Kwic: " + this.toString());
		
		this.knownPrefixes.forEach(
				pref -> {
					List<String> content = this.tokens.stream().map(t -> t.getProperty(pref)).collect(Collectors.toList());
					kwic.tokenProperties.put(pref,content);
				 }
				);
		
		return kwic;
	}
}
