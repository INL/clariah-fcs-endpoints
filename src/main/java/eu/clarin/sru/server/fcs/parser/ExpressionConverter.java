package eu.clarin.sru.server.fcs.parser;

import java.util.*;
import java.util.stream.Collectors;

import clariah.fcs.mapping.Conversion;
import clariah.fcs.mapping.Feature;
import clariah.fcs.mapping.FeatureConjunction;

import eu.clarin.sru.server.fcs.parser.Expression;
import eu.clarin.sru.server.fcs.parser.ExpressionAnd;
import eu.clarin.sru.server.fcs.parser.ExpressionGroup;
import eu.clarin.sru.server.fcs.parser.ExpressionNot;
import eu.clarin.sru.server.fcs.parser.ExpressionOr;

public class ExpressionConverter implements ExpressionRewriter
{
	private Conversion conversion;
	
	public ExpressionConverter(Conversion conversion)
	{
		this.conversion = conversion;
	}
	
	
	public QueryNode featureNode(Feature f)
	{
		List<QueryNode> orz = f.values.stream().map(v -> new Expression(null, f.name, Operator.EQUALS, v, null)).collect(Collectors.toList());
		if (orz.size() == 1)
			return orz.get(0);
		ExpressionOr eo = new ExpressionOr(orz);
		return eo;
	}
	
	@Override
	public QueryNode rewriteExpression(Expression e) // TODO: if the operator is a NOT_EQUALS, this is too simple
	{
	
		String f = e.getLayerIdentifier();
		String v = e.getRegexValue();
		// System.err.println("Expression: "  + f + "=" + v);
	    Set<FeatureConjunction> fcs = conversion.translateFeature(f, v);
	  
	    
	    List<QueryNode> orz = new ArrayList<>();
	    
	    for (FeatureConjunction fc: fcs)
	    {
	    	List<QueryNode> andz = fc.features().map(feat -> featureNode(feat)).collect(Collectors.toList());
	    	if (andz.size() == 1)
	    		orz.add(andz.get(0));
	    	else
	    	{
	    		ExpressionAnd ea = new ExpressionAnd(andz);
	    		orz.add(ea);
	    	}
	    }
	    if (orz.size() == 1)
	    {
	    	QueryNode o1 =  orz.get(0);
	    	if (o1 instanceof Expression && e.getOperator() == Operator.NOT_EQUALS)
	    	{
	    		Expression e1 = (Expression) o1;
	    		// clone e and make negative
	    	}
	    	return orz.get(0);
	    }
		return new ExpressionOr(orz); // TODO: wrap in NOT if nonatomic translation and operator = NOT_EQUALS
	}
}
