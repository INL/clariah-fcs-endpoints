package clariah.fcs.mapping;

import java.util.*;

import eu.clarin.sru.server.fcs.parser.Expression;
import eu.clarin.sru.server.fcs.parser.ExpressionAnd;
import eu.clarin.sru.server.fcs.parser.ExpressionGroup;
import eu.clarin.sru.server.fcs.parser.ExpressionNot;
import eu.clarin.sru.server.fcs.parser.ExpressionOr;
import eu.clarin.sru.server.fcs.parser.ExpressionWildcard;
import eu.clarin.sru.server.fcs.parser.QueryDisjunction;
import eu.clarin.sru.server.fcs.parser.QueryGroup;
import eu.clarin.sru.server.fcs.parser.QueryNode;
import eu.clarin.sru.server.fcs.parser.QueryParser;
import eu.clarin.sru.server.fcs.parser.QuerySegment;
import eu.clarin.sru.server.fcs.parser.QuerySequence;
import eu.clarin.sru.server.fcs.parser.QueryVisitor;
import eu.clarin.sru.server.fcs.parser.SimpleWithin;

/**
 * Hm.
 * Already at this level, a single feature might translate to a disjunction or a conjunction 
 * of other features, or a disjunction of conjunctions. 
 * this should also cover translation of word= to t_lc= for Nederlab, etc.
 */

public abstract class Conversion
{
	String tagsetFrom;
	String tagsetTo;
	
	public abstract Set<String> translatePoS(String PoS);
	public abstract Set<FeatureConjunction> translateFeature(String feature, String value);
	
	
	
	
	public  static void bla(String q)
	{
		QueryParser parser = new QueryParser();
		try
		{
			QueryNode qn = parser.parse(q);
			System.out.println(qn.toString());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


