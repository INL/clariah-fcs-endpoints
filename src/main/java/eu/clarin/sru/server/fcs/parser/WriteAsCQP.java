package eu.clarin.sru.server.fcs.parser;

import java.util.*;
import java.util.stream.Collectors;

import org.ivdnt.util.StringUtils;

public class WriteAsCQP 
{
	boolean useRegex = false;
	
	String posTagFeature;
	Set<String> grammaticalFeatures;
	String valueQuote = "'";
	
	public void setRegexHack(String posTagFeature, String[] grammaticalFeatures)
	{
		this.posTagFeature = posTagFeature;
		this.grammaticalFeatures = new HashSet<>();
		for (String s: grammaticalFeatures)
			this.grammaticalFeatures.add(s);
		this.useRegex = true;
	}
	
	public void setQuote(String s)
	{
		this.valueQuote = s;
	}
	
	public List<String> writeList(List<QueryNode> l)
	{
		return l.stream().map(n -> writeAsCQP(n)).collect(Collectors.toList());
	}
	
	public  String writeAsCQP(QueryNode node)
	{
		String n1;
		if (node instanceof QueryDisjunction) {
			n1=writeQueryDisjunction((QueryDisjunction) node);
		} else if (node instanceof QueryGroup) {
			n1=writeQueryGroup((QueryGroup) node);
		} else if (node instanceof QuerySegment) {
			n1=writeQuerySegment((QuerySegment) node);
		} else if (node instanceof QuerySequence) {
			n1=writeQuerySequence((QuerySequence) node);
		} else if (node instanceof ExpressionAnd) {
			n1=writeExpressionAnd((ExpressionAnd) node);
		} else if (node instanceof Expression) {
			n1=writeExpression( (Expression) node );
		} else if (node instanceof ExpressionGroup) {
			n1=writeExpressionGroup((ExpressionGroup) node);
		} else if (node instanceof ExpressionNot) {
			n1=writeExpressionNot((ExpressionNot) node);
		} else if (node instanceof ExpressionOr) {
			n1=writeExpressionOr((ExpressionOr) node);
		} else if (node instanceof ExpressionWildcard) {
			n1=writeExpressionWildcard((ExpressionWildcard) node);
		} else if (node instanceof SimpleWithin) {
			n1=writeSimpleWithin((SimpleWithin) node);
		} else {
			throw new RuntimeException("unexpected node type: "  + node.getNodeType());
		}
		return n1;
	}
	
	private   String writeSimpleWithin(SimpleWithin node) {
		SimpleWithin sw =  new SimpleWithin(node.getScope());
		return " within <snapikniet/>" ; // children ???? TODO dit kan niet kloppen!!!
	}

	private   String writeExpressionWildcard(ExpressionWildcard node) {
		return "" ; // TODO snap ik dit?? De parser maakt een QuerySegment (Wildcard) aan
	}

	private   String writeExpressionOr(ExpressionOr node) {
		
		return StringUtils.join(writeList(node.getChildren()), " | ");
	}

	private   String writeExpressionNot(ExpressionNot node) {
		return "!" + writeAsCQP(node.getFirstChild());
	}

	private   String writeExpressionGroup(ExpressionGroup node) {
		return "("  + writeAsCQP(node.getFirstChild()) + ")";
	}

	private String writeExpression(Expression node) 
	{
		Expression e = new Expression(node.getLayerQualifier(), node.getLayerIdentifier(), node.getOperator(), node.getRegexValue(), node.getRegexFlags());
		String n = e.getLayerIdentifier();
		String v = e.getRegexValue();
		
		if (this.useRegex && n.equals(posTagFeature))
			return  String.format("%s=%s^(%s).*%s",n,valueQuote,v, valueQuote);
		if (this.useRegex && this.grammaticalFeatures.contains(n))
			 return String.format("%s=%s.*%s=(%s).*%s", posTagFeature, valueQuote, n, v, valueQuote);
	
		return n +  '='  + valueQuote + v+ valueQuote;
	}

	private   String writeExpressionAnd(ExpressionAnd node) {
		return StringUtils.join(writeList(node.getChildren()), " & ");
	}

	private   String writeQuerySequence(QuerySequence node) {
		return StringUtils.join(writeList(node.getChildren()), " ");
	}

	private   String writeQuerySegment(QuerySegment node) {
		String arg0 = "[" + writeAsCQP(node.getExpression()) + "]";
		int min = node.getMinOccurs();
		int max = node.getMaxOccurs();
		if (min ==1 && max==1)
			return arg0;
		return String.format("%s{%d, %d}", arg0, min, max);
	}

	private   String writeQueryGroup(QueryGroup node) 
	{
		String arg0 = "(" + writeAsCQP(node.getFirstChild()) + ")";
		int min = node.getMinOccurs();
		int max = node.getMaxOccurs();
		if (min ==1 && max==1)
			return arg0;
		return String.format("%s{%d, %d}", arg0, min, max);
	}

	private   String writeQueryDisjunction(QueryDisjunction node) 
	{
		return StringUtils.join(writeList(node.getChildren()), " | ");
	}
}
