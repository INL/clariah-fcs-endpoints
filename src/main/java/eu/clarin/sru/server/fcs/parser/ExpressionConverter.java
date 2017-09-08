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
	Conversion conversion;
	public ExpressionConverter(Conversion conversion)
	{
		this.conversion = conversion;
	}
	
	// Expression e = new Expression(node.getLayerQualifier(), node.getLayerIdentifier(), node.getOperator(), node.getRegexValue(), node.getRegexFlags());
	
	public QueryNode featureNode(Feature f)
	{
		List<QueryNode> orz = f.values.stream().map(v -> new Expression(null, f.name, Operator.EQUALS, v, null)).collect(Collectors.toList());
		ExpressionOr eo = new ExpressionOr(orz);
		return eo;
	}
	
	@Override
	public QueryNode rewriteExpression(Expression e) 
	{
		// TODO Auto-generated method stub
		String f = e.getLayerIdentifier();
		String v = e.getRegexValue();
	    Set<FeatureConjunction> fcs = conversion.translateFeature(f, v);
	    // now create an or node of and nodes
	    
	    List<QueryNode> orz = new ArrayList<>();
	    
	    for (FeatureConjunction fc: fcs)
	    {
	    	List<QueryNode> andz = fc.features().map(feat -> featureNode(feat)).collect(Collectors.toList());
	    	ExpressionAnd ea = new ExpressionAnd(andz);
	    	orz.add(ea);
	    }
	    
		return new ExpressionOr(orz);
	}
}
