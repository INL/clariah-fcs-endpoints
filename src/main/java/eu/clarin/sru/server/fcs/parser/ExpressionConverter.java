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

/**
 
 Simple-minded atomic expression rewriter.<br>
 
 negation stuff and amount of brackets still feel fragile<br>

 this works now:<br>
 UD: [word="taalman"][pos!="PROPN" & word="kip"]<br>
 nederlab: [t_lc="taalman"] [!((pos="SPEC" & feat.spectype="deeleigen") | (pos="N" & feat.ntype="eigen")) & t_lc="kip"]<br>
 opensonar: [word="taalman"] [!((pos="^(SPEC).*" & pos=".*(deeleigen).*") | (pos="^(N).*" & pos=".*(eigen).*")) & word="kip"]<br>
 
 * @author jesse
 *
 */
public class ExpressionConverter implements ExpressionRewriter
{
	private Conversion conversion;
	
	public ExpressionConverter(Conversion conversion)
	{
		this.conversion = conversion;
	}
	
	
	private QueryNode featureNode(Feature f)
	{
		List<QueryNode> orz = f.values.stream().map(v -> new Expression(null, f.name, Operator.EQUALS, v, null)).collect(Collectors.toList());
		if (orz.size() == 1)
			return orz.get(0);
		ExpressionOr eo = new ExpressionOr(orz);
		return eo;
	}
	
	private QueryNode negation(QueryNode n)
	{
		if (n instanceof Expression) // flip if simple expression
    	{
		
    		Expression e1 = (Expression) n;
    		Operator flip = (e1.getOperator() == Operator.NOT_EQUALS)? Operator.EQUALS: Operator.NOT_EQUALS;
    		
    		Expression e2 = new Expression(e1.getLayerQualifier(), e1.getLayerIdentifier(), flip, e1.getRegexValue(), e1.getRegexFlags());
    		// clone e and make negative
    		return e2;
    	}
		return new ExpressionNot(n); // kan je natuurlijk naar binnen proberen te duwen, etc, maar laat maar
	}
	
	@Override
	public QueryNode rewriteExpression(Expression e) // TODO: if the operator is a NOT_EQUALS, this is too simple
	{
	    final boolean negative = e.getOperator() == Operator.NOT_EQUALS;
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
	    	return negative?negation(o1): o1;
	    }
	    ExpressionOr orrie = new ExpressionOr(orz);
		return negative?negation(orrie):orrie; // TODO: wrap in NOT if nonatomic translation and operator = NOT_EQUALS
	}
}
